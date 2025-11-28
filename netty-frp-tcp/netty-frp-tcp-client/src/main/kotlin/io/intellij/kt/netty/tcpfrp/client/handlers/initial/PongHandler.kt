package io.intellij.kt.netty.tcpfrp.client.handlers.initial

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.tcpfrp.protocol.channel.DispatchManager
import io.intellij.kt.netty.tcpfrp.protocol.channel.FrpChannel
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
        val frpChannel: FrpChannel = FrpChannel.getBy(ctx.channel())
        DispatchManager.buildIn(ctx.channel())

        // 5s ping
        ctx.channel().attr(PING_KEY).set(
            ctx.executor().scheduleAtFixedRate(
                {
                    frpChannel.writeAndFlush(Ping.create("frp-client"))
                },
                1, 5, TimeUnit.SECONDS
            )
        )

        log.info("[channelActive]: Pong Handler")
        // must but just once
        frpChannel.read()
    }

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: Pong?) {
        log.info("HeatBeat PONG|{}", msg)
        FrpChannel.getBy(ctx.channel()).read()
    }

    @Throws(Exception::class)
    override fun channelInactive(ctx: ChannelHandlerContext) {
        log.warn("stop scheduled ping ...")
        val scheduledFuture = ctx.channel().attr(PING_KEY).get()
        scheduledFuture.cancel(true)

        FrpChannel.getBy(ctx.channel()).close()

        super.channelInactive(ctx)
    }

}
