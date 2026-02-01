package io.intellij.kt.netty.tcpfrp.client

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.tcpfrp.client.handlers.FrpClientInitializer
import io.intellij.kt.netty.tcpfrp.commons.EventLoopGroups
import io.intellij.kt.netty.tcpfrp.config.ClientConfig
import io.intellij.kt.netty.tcpfrp.protocol.channel.FrpChannel
import io.intellij.kt.netty.tcpfrp.protocol.client.AuthRequest
import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import java.util.concurrent.TimeUnit

/**
 * FrpClient
 *
 * @author tech@intellij.io
 */
class FrpClient private constructor(
    private val config: ClientConfig,
    private val allowReconnect: Boolean
) {

    companion object {
        private val log = getLogger(FrpClient::class.java)

        fun start(config: ClientConfig, allowReconnect: Boolean = true) {
            FrpClient(config, allowReconnect).start()
        }

    }

    private val b = Bootstrap()
    private val eventLoopGroup: EventLoopGroup = EventLoopGroups.get().getWorkerGroup(2)

    init {
        b.group(eventLoopGroup)
            .channel(NioSocketChannel::class.java)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .handler(FrpClientInitializer(config))
    }

    fun start() {
        doStart(0)
    }

    private fun doStart(count: Int) {
        val host: String = config.serverHost
        val port: Int = config.serverPort
        val connectFuture = b.connect(host, port)

        connectFuture.addListener(ChannelFutureListener { future: ChannelFuture ->
            if (future.isSuccess) {
                log.info("[CONNECT] connect to frp-server success|host={} |port={}", host, port)
                val ch = future.channel()
                val frpChannel: FrpChannel = FrpChannel.getBy(ch)
                log.info("Send Auth Request")
                frpChannel.writeAndFlush(AuthRequest.build(config.authToken), { f ->
                    if (f.isSuccess) {
                        // for read
                        f.channel().pipeline().fireChannelActive()
                    }
                })

                val closeFuture = ch.closeFuture()
                if (allowReconnect) {
                    // detect channel close then restart
                    closeFuture.addListener(ChannelFutureListener { detectFuture: ChannelFuture ->
                        eventLoopGroup.execute { this.doStart(0) }
                    })
                }
            } else if (allowReconnect) {
                eventLoopGroup.schedule(
                    {
                        log.warn("[RECONNECT] reconnect to frp-server <{}:{}> | count={}", host, port, count)
                        this.doStart(count + 1)
                    }, 3, TimeUnit.SECONDS
                )
            } else {
                log.error("Connect to frp-server <{}:{}> failed.", host, port)
                log.error("Exit...")
                this.stop()
            }
        })

        val closeFuture = connectFuture.channel().closeFuture()
        try {
            closeFuture.sync()
            log.error("[RECONNECT] lost connection to frp-server | count={}", count)
        } catch (e: InterruptedException) {
            log.error("closeFuture.sync()|errorMsg={}", e.message)
        } finally {
            if (!allowReconnect) {
                this.stop()
            }
        }
    }

    fun stop() {
        log.warn("eventLoopGroup.shutdownGracefully()...")
        eventLoopGroup.shutdownGracefully()
    }

}