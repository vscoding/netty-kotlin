package io.intellij.kt.netty.tcp.lb.handlers

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.commons.utils.ChannelUtils
import io.intellij.kt.netty.tcp.lb.config.Backend
import io.intellij.kt.netty.tcp.lb.handlers.FrontendInboundHandler.Companion.OUTBOUND_CHANNEL_KEY
import io.intellij.kt.netty.tcp.lb.selector.BackendSelector
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.socket.SocketChannel

/**
 * BootstrapLoopConnector
 *
 * @author tech@intellij.io
 */
class BootstrapLoopConnector(
    val selector: BackendSelector,
    val inboundChannel: Channel
) {
    private val b = Bootstrap()

    companion object {
        private val log = getLogger(BootstrapLoopConnector::class.java)
    }

    fun connect() {
        b.group(inboundChannel.eventLoop())
            .channel(inboundChannel.javaClass)
            .handler(object : ChannelInitializer<SocketChannel>() {
                @Throws(Exception::class)
                override fun initChannel(ch: SocketChannel) {
                    // do nothing, just for init a pipeline
                    // 避免 BackendOutboundHandler 需要 @Sharable
                }
            })
            .option(ChannelOption.AUTO_READ, false)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)

        this.loopConnect(selector.select())
    }

    private fun loopConnect(backend: Backend?) {
        if (backend == null) {
            log.error("No available backend server to connect")
            ChannelUtils.closeOnFlush(inboundChannel)
            return
        }

        val f: ChannelFuture = b.connect(backend.host, backend.port)
        f.addListener(
            ChannelFutureListener { channelFuture: ChannelFuture ->
                if (channelFuture.isSuccess) {
                    log.info("connect to backend success: {}", backend)
                    val outboundChannel = channelFuture.channel()
                    inboundChannel.attr(OUTBOUND_CHANNEL_KEY).set(outboundChannel)

                    outboundChannel.pipeline()
                        .addLast(BackendOutboundHandler(inboundChannel, selector, backend))

                    // read after connected
                    inboundChannel.read()
                } else {
                    log.error("connect to backend failed: {}", channelFuture.cause().message)
                    val next: Backend? = selector.nextIfConnectFailed(backend)

                    if (next != null) {
                        loopConnect(next)
                    } else {
                        log.error("No available backend server After all failed")
                        ChannelUtils.closeOnFlush(inboundChannel)
                    }
                }
            }
        )
    }
}
