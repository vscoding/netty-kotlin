package io.intellij.kt.netty.tcpfrp.server.handlers.dispatch

import io.intellij.kt.netty.tcpfrp.commons.Listeners
import io.intellij.kt.netty.tcpfrp.protocol.channel.DispatchManager
import io.intellij.kt.netty.tcpfrp.protocol.channel.DispatchPacket
import io.intellij.kt.netty.tcpfrp.protocol.channel.FrpChannel
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
        DispatchManager.getBy(ctx.channel()).dispatch(msg, Listeners.read())
    }

    @Throws(Exception::class)
    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        FrpChannel.getBy(ctx.channel()).read()
    }

}
