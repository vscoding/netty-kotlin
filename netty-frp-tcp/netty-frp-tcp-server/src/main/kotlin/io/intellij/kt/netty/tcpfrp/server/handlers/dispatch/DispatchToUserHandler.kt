package io.intellij.kt.netty.tcpfrp.server.handlers.dispatch

import io.intellij.kt.netty.tcpfrp.commons.Listeners
import io.intellij.kt.netty.tcpfrp.protocol.channel.DispatchPacket
import io.intellij.kt.netty.tcpfrp.protocol.channel.getDispatchManager
import io.intellij.kt.netty.tcpfrp.protocol.channel.getFrpChannel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

/**
 * DispatchToUserHandler
 *
 * @author tech@intellij.io
 */
class DispatchToUserHandler : SimpleChannelInboundHandler<DispatchPacket>() {

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: DispatchPacket) {
        // after UserChannel read0
        ctx.channel().getDispatchManager().dispatch(msg, Listeners.read())
    }

    @Throws(Exception::class)
    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.channel().getFrpChannel().activeRead()
    }

}
