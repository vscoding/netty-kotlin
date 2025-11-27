package io.intellij.netty.server.socks.handlers.connect

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.util.concurrent.Promise

/**
 * DirectClientHandler
 *
 * @author tech@intellij.io
 */
class DirectClientHandler(val promise: Promise<Channel>) : ChannelInboundHandlerAdapter() {

    override fun channelActive(ctx: ChannelHandlerContext) {
        // Remove the handler from the pipeline as soon as the connection is established
        ctx.pipeline().remove(this)
        promise.setSuccess(ctx.channel())
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext?, throwable: Throwable?) {
        promise.setFailure(throwable)
    }
}
