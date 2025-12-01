package io.intellij.kt.netty.commons.handlers

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.commons.utils.CtxUtils
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

/**
 * DirectEchoHandler
 *
 * @author tech@intellij.io
 */
class DirectEchoHandler : ChannelInboundHandlerAdapter() {
    companion object {
        private val log = getLogger(DirectEchoHandler::class.java)
    }

    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        ctx.write(msg)
    }

    @Throws(Exception::class)
    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.flush()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        log.warn("connection closed by remote peer:{}|cause={}", CtxUtils.getRemoteAddress(ctx), cause.message)
        ctx.close()
    }

}