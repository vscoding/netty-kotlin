package io.intellij.kt.netty.server.test.handlers

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.commons.utils.CtxUtils
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

/**
 * ConnectionHandler
 *
 * @author tech@intellij.io
 */
class ConnectionHandler : ChannelInboundHandlerAdapter() {
    companion object {
        private val log = getLogger(ConnectionHandler::class.java)
    }

    @Throws(Exception::class)
    override fun channelActive(ctx: ChannelHandlerContext) {
        log.info("Receive client connect|{}", CtxUtils.getRemoteAddress(ctx))
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        // Pass to the next handler
        ctx.fireChannelRead(msg)
    }

    @Throws(Exception::class)
    override fun channelInactive(ctx: ChannelHandlerContext) {
        log.warn("Client disconnected|{}", CtxUtils.getRemoteAddress(ctx))
    }

}