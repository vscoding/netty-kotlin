package io.intellij.kt.netty.tcpfrp.client.service

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.util.concurrent.Promise

/**
 * DirectServiceHandler
 *
 * @author tech@intellij.io
 */
class DirectServiceHandler(val promise: Promise<Channel>) : ChannelInboundHandlerAdapter() {

    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.pipeline().remove(this)
        promise.setSuccess(ctx.channel())
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext?, throwable: Throwable) {
        promise.setFailure(throwable)
    }

}
