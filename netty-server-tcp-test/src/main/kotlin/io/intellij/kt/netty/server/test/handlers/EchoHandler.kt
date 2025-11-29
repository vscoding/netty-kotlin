package io.intellij.kt.netty.server.test.handlers

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.server.test.LogBytesUtils
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

/**
 * EchoHandlers
 *
 * @author tech@intellij.io
 */
class EchoHandler : SimpleChannelInboundHandler<ByteBuf>() {
    companion object {
        private val log = getLogger(EchoHandler::class.java)
    }

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: ByteBuf) {
        val len = msg.readableBytes()
        val bytes = ByteArray(len)
        msg.readBytes(bytes)

        // log bytes
        LogBytesUtils.printBytes(bytes, log)

        // echo back
        ctx.writeAndFlush(Unpooled.wrappedBuffer(bytes))
    }

}