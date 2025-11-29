package io.intellij.kt.netty.server.test.handlers

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.server.test.LogBytesUtils
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

/**
 * LogHandler
 *
 * @author tech@intellij.io
 */
class LogHandler : SimpleChannelInboundHandler<ByteBuf>() {
    companion object {
        private val log = getLogger(LogHandler::class.java)
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: ByteBuf) {
        val len = msg.readableBytes()
        val bytes = ByteArray(len)
        msg.readBytes(bytes)

        LogBytesUtils.printBytes(bytes, log)
    }

}