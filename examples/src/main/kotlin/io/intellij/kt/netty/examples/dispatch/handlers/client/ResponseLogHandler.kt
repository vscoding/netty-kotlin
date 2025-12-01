package io.intellij.kt.netty.examples.dispatch.handlers.client

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.examples.dispatch.model.msg.Response
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

/**
 * ResponseLogHandler
 *
 * @author tech@intellij.io
 */
class ResponseLogHandler : SimpleChannelInboundHandler<Response>() {

    companion object {
        private val log = getLogger(ResponseLogHandler::class.java)
    }

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: Response) {
        log.info("response|{}", msg)
    }
}
