package io.intellij.kt.netty.examples.dispatch.handlers.server

import com.alibaba.fastjson2.JSON
import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.examples.dispatch.model.DataBody
import io.intellij.kt.netty.examples.dispatch.model.msg.LoginReq
import io.intellij.kt.netty.examples.dispatch.model.msg.LogoutReq
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

/**
 * ServerDataBodyHandler
 *
 * @author tech@intellij.io
 */
class ServerDataBodyHandler : SimpleChannelInboundHandler<DataBody>() {
    companion object {
        private val log = getLogger(ServerDataBodyHandler::class.java)
    }

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: DataBody) {
        when (msg.dataType) {
            1 -> {
                val loginReq =
                    JSON.parseObject(msg.json, LoginReq::class.java) ?: throw RuntimeException("LoginReq is null")
                ctx.channel().attr(Attributes.USERNAME).set(loginReq.username)
                ctx.fireChannelRead(loginReq)
            }

            2 -> {
                val logoutReq =
                    JSON.parseObject(msg.json, LogoutReq::class.java) ?: throw RuntimeException("LogoutReq is null")
                ctx.channel().attr(Attributes.USERNAME).set(logoutReq.username)
                ctx.fireChannelRead(logoutReq)
            }

            else -> {
                throw RuntimeException("Unknown data type")
            }
        }
    }

    @Throws(Exception::class)
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        log.error("DataBodyHandler error|{}", cause.message)
        ctx.close()
    }
}
