package io.intellij.kt.netty.tcpfrp.server

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.tcpfrp.commons.EventLoopGroups
import io.intellij.kt.netty.tcpfrp.config.ServerConfig
import io.intellij.kt.netty.tcpfrp.server.handlers.FrpServerInitializer
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelOption
import io.netty.channel.socket.nio.NioServerSocketChannel

/**
 * FrpServer
 *
 * @author tech@intellij.io
 */
class FrpServer(private val config: ServerConfig) {

    companion object {
        private val log = getLogger(FrpServer::class.java)

        @JvmStatic
        fun start(config: ServerConfig) {
            val server = FrpServer(config)
            server.start()
        }
    }

    private val b = ServerBootstrap()
    val boss = EventLoopGroups.get().getBossGroup()
    val worker = EventLoopGroups.get().getWorkerGroup()

    init {

        b.group(boss, worker)
            .channel(NioServerSocketChannel::class.java)
            .childOption<Boolean?>(ChannelOption.SO_KEEPALIVE, true)
            .childHandler(FrpServerInitializer(config))
    }

    fun start() {
        try {
            val f: ChannelFuture = b.bind(config.port).sync()
            f.addListener(ChannelFutureListener { cf: ChannelFuture ->
                if (cf.isSuccess) {
                    log.info("frp server started on port {}", config.port)
                } else {
                    log.error("frp server start failed", cf.cause())
                }
            })
            f.channel().closeFuture().sync()
        } catch (e: InterruptedException) {
            log.error("frp server start failed", e)
        } finally {
            boss.shutdownGracefully()
            worker.shutdownGracefully()
        }
    }

}