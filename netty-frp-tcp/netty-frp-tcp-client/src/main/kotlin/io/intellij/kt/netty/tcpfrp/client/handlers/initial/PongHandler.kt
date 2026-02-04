package io.intellij.kt.netty.tcpfrp.client.handlers.initial

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.tcpfrp.protocol.channel.FrpChannel
import io.intellij.kt.netty.tcpfrp.protocol.channel.getFrpChannel
import io.intellij.kt.netty.tcpfrp.protocol.channel.setDispatchManager
import io.intellij.kt.netty.tcpfrp.protocol.heartbeat.Ping
import io.intellij.kt.netty.tcpfrp.protocol.heartbeat.Pong
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.util.AttributeKey
import io.netty.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * PongHandler
 *
 * @author tech@intellij.io
 */
class PongHandler : SimpleChannelInboundHandler<Pong>() {

    companion object {
        private val log = getLogger(PongHandler::class.java)
        private val PING_KEY = AttributeKey.valueOf<ScheduledFuture<*>>("ping")
    }

    /**
     * Triggered from [ListeningResponseHandler]
     */
    @Throws(Exception::class)
    override fun channelActive(ctx: ChannelHandlerContext) {
        val ch = ctx.channel()
        val frpChannel: FrpChannel = ch.getFrpChannel()
        ch.setDispatchManager()

        // 5s ping
        ctx.channel().attr(PING_KEY).set(
            ctx.executor().scheduleAtFixedRate(
                {
                    frpChannel.writeAndFlush(Ping.build("frp-client"))
                },
                1, 5, TimeUnit.SECONDS
            )
        )

        log.info("[channelActive]: Pong Handler")
        // must but just once
        frpChannel.activeRead()
    }

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: Pong?) {
        log.info("HeatBeat PONG|{}", msg)
        ctx.channel().getFrpChannel().activeRead()
    }

    @Throws(Exception::class)
    override fun channelInactive(ctx: ChannelHandlerContext) {
        log.warn("stop scheduled ping ...")
        val scheduledFuture = ctx.channel().attr(PING_KEY).get()
        scheduledFuture.cancel(true)

        ctx.channel().getFrpChannel().close()

        super.channelInactive(ctx)
    }

}
