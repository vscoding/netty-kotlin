package io.intellij.kt.netty.examples.dispatch.handlers.client

import com.alibaba.fastjson2.JSON
import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.examples.dispatch.model.DataBody
import io.intellij.kt.netty.examples.dispatch.model.msg.Response
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

/**
 * ClientDataBodyHandler
 *
 * @author tech@intellij.io
 */
class ClientDataBodyHandler : SimpleChannelInboundHandler<DataBody>() {

    companion object {
        private val log = getLogger(ClientDataBodyHandler::class.java)
    }

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: DataBody) {
        if (msg.dataType == 3) {
            val response =
                JSON.parseObject(msg.json, Response::class.java) ?: throw RuntimeException("Response is null")
            ctx.fireChannelRead(response)
        } else {
            throw RuntimeException("Unknown data type")
        }
    }

    @Throws(Exception::class)
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        log.error("DataBodyHandler error|{}", cause.message)
        ctx.close()
    }
}
