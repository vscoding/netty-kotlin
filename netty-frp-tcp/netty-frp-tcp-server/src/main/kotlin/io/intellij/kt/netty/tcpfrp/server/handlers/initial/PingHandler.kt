package io.intellij.kt.netty.tcpfrp.server.handlers.initial

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.tcpfrp.protocol.channel.FrpChannel
import io.intellij.kt.netty.tcpfrp.protocol.channel.getDispatchManager
import io.intellij.kt.netty.tcpfrp.protocol.channel.getFrpChannel
import io.intellij.kt.netty.tcpfrp.protocol.channel.setDispatchManager
import io.intellij.kt.netty.tcpfrp.protocol.heartbeat.Ping
import io.intellij.kt.netty.tcpfrp.protocol.heartbeat.Pong
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

/**
 * PingHandler
 *
 * @author tech@intellij.io
 */
class PingHandler : SimpleChannelInboundHandler<Ping>() {

    companion object {
        private val log = getLogger(PingHandler::class.java)
    }

    /**
     * Triggered from [ListeningRequestHandler]
     */
    @Throws(Exception::class)
    override fun channelActive(ctx: ChannelHandlerContext) {
        log.info("[channelActive]: Ping Handler")
        ctx.channel().setDispatchManager()
        ctx.channel().getFrpChannel().activeRead()
    }

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, ping: Ping) {
        log.info("HeatBeat PING|{}", ping)
        val frpChannel: FrpChannel = ctx.channel().getFrpChannel()
        frpChannel.write(Pong.build(ping.name))
    }

    @Throws(Exception::class)
    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.channel().getFrpChannel()
            .flush()
            .activeRead()
    }

    @Throws(Exception::class)
    override fun channelInactive(ctx: ChannelHandlerContext) {
        log.warn("release dispatch channel")
        val frpChannel: FrpChannel = ctx.channel().getFrpChannel()
        frpChannel.getDispatchManager().releaseAll()
        super.channelInactive(ctx)

        log.warn("close frp channel")
        frpChannel.close()
    }
}
