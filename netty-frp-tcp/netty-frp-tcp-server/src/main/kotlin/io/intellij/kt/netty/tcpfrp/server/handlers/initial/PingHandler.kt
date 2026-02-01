package io.intellij.kt.netty.tcpfrp.server.handlers.initial

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.tcpfrp.protocol.channel.DispatchManager
import io.intellij.kt.netty.tcpfrp.protocol.channel.FrpChannel
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
        DispatchManager.buildIn(ctx.channel())
        FrpChannel.getBy(ctx.channel()).read()
    }

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, ping: Ping) {
        log.info("HeatBeat PING|{}", ping)
        val frpChannel: FrpChannel = FrpChannel.getBy(ctx.channel())
        frpChannel.write(Pong.build(ping.name))
    }

    @Throws(Exception::class)
    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        FrpChannel.getBy(ctx.channel()).flush().read()
    }

    @Throws(Exception::class)
    override fun channelInactive(ctx: ChannelHandlerContext) {
        log.warn("release dispatch channel")
        val frpChannel: FrpChannel = FrpChannel.getBy(ctx.channel())
        DispatchManager.getBy(frpChannel.getBy()).releaseAll()
        super.channelInactive(ctx)

        log.warn("close frp channel")
        frpChannel.close()
    }
}
