package io.intellij.kt.netty.tcpfrp.client.handlers.dispatch

import io.intellij.kt.netty.tcpfrp.commons.Listeners
import io.intellij.kt.netty.tcpfrp.protocol.channel.DispatchPacket
import io.intellij.kt.netty.tcpfrp.protocol.channel.getDispatchManager
import io.intellij.kt.netty.tcpfrp.protocol.channel.getFrpChannel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

/**
 * DispatchToServiceHandler
 *
 * @author tech@intellij.io
 */
class DispatchToServiceHandler : SimpleChannelInboundHandler<DispatchPacket>() {

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: DispatchPacket) {
        // 获取到数据包，e.g. user --- frp-server:3306 的数据包
        ctx.channel().getDispatchManager().dispatch(msg, Listeners.read())
    }

    @Throws(Exception::class)
    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.channel().getFrpChannel()
            .activeRead()
    }

    @Throws(Exception::class)
    override fun channelInactive(ctx: ChannelHandlerContext) {
        ctx.channel().getDispatchManager().releaseAll()
    }

}
