package io.intellij.kt.netty.examples.dispatch.handlers.server

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.examples.dispatch.model.msg.LogoutReq
import io.intellij.kt.netty.examples.dispatch.model.msg.Response
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

/**
 * LogoutHandler
 *
 * @author tech@intellij.io
 */
class LogoutHandler : SimpleChannelInboundHandler<LogoutReq>() {
    companion object {
        private val log = getLogger(LogoutHandler::class.java)
    }

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: LogoutReq?) {
        val username =
            ctx.channel().attr(Attributes.USERNAME).get() ?: throw RuntimeException("logout username is null")
        log.info("logout|attrLoginUsername={}|{}", username, msg)
        log.info("logout business logic ...")

        ctx.writeAndFlush(Response.create(200, "logout success"))
    }
}
