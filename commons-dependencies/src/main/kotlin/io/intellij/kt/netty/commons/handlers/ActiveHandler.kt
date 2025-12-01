package io.intellij.kt.netty.commons.handlers

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.commons.utils.CtxUtils
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

/**
 * ActiveHandler
 *
 * @author tech@intellij.io
 */
class ActiveHandler : ChannelInboundHandlerAdapter() {
    companion object {
        private val log = getLogger(ActiveHandler::class.java)
    }

    @Throws(Exception::class)
    override fun channelActive(ctx: ChannelHandlerContext) {
        log.info("Receive client connect|{}", CtxUtils.getRemoteAddress(ctx))
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        // Pass to the next handler
        ctx.pipeline().remove(this)
        ctx.fireChannelRead(msg)
    }

}