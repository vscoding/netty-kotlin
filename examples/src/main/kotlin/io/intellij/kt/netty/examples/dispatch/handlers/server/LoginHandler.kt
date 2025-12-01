package io.intellij.kt.netty.examples.dispatch.handlers.server

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.examples.dispatch.model.msg.LoginReq
import io.intellij.kt.netty.examples.dispatch.model.msg.Response
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

/**
 * LoginHandler
 *
 * @author tech@intellij.io
 */
class LoginHandler : SimpleChannelInboundHandler<LoginReq>() {

    companion object {
        private val log = getLogger(LoginHandler::class.java)
    }

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: LoginReq) {
        val username =
            ctx.channel().attr(Attributes.USERNAME).get() ?: throw RuntimeException("login username is null")
        log.info("login|attrLoginUsername={}|{}", username, msg)
        log.info("login business logic ...")
        ctx.writeAndFlush(Response.create(200, "login success"))
    }

}
