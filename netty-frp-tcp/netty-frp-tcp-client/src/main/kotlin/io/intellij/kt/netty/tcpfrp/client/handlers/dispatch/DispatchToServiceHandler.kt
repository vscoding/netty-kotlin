package io.intellij.kt.netty.tcpfrp.client.handlers.dispatch

import io.intellij.kt.netty.tcpfrp.commons.Listeners
import io.intellij.kt.netty.tcpfrp.protocol.channel.DispatchManager
import io.intellij.kt.netty.tcpfrp.protocol.channel.DispatchPacket
import io.intellij.kt.netty.tcpfrp.protocol.channel.FrpChannel
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
        DispatchManager.getFromCh(ctx.channel()).dispatch(msg, Listeners.read())
    }

    @Throws(Exception::class)
    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        FrpChannel.getBy(ctx.channel()).read()
    }

    @Throws(Exception::class)
    override fun channelInactive(ctx: ChannelHandlerContext) {
        DispatchManager.getFromCh(ctx.channel()).releaseAll()
    }

}
