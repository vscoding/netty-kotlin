package io.intellij.kt.netty.tcp.lb.handlers

import io.intellij.kt.netty.commons.utils.ChannelUtils
import io.intellij.kt.netty.tcp.lb.config.Backend
import io.intellij.kt.netty.tcp.lb.selector.BackendSelector
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

/**
 * BackendOutboundHandler
 *
 * @author tech@intellij.io
 */
class BackendOutboundHandler(
    val inboundChannel: Channel,
    val selector: BackendSelector,
    val target: Backend
) : ChannelInboundHandlerAdapter() {

    @Throws(Exception::class)
    override fun channelActive(ctx: ChannelHandlerContext) {
        selector.onChannelActive(target)
        ctx.read()
    }

    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val outbound = ctx.channel()
        inboundChannel.writeAndFlush(msg).addListener(
            ChannelFutureListener { future: ChannelFuture ->
                if (future.isSuccess) {
                    outbound.read()
                } else {
                    future.channel().close()
                }
            }
        )
    }

    @Throws(Exception::class)
    override fun channelInactive(ctx: ChannelHandlerContext) {
        selector.onChannelInactive(target)
        ChannelUtils.closeOnFlush(inboundChannel)
    }

    @Throws(Exception::class)
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        ChannelUtils.closeOnFlush(ctx.channel())
    }
}
