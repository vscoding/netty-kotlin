package io.intellij.kt.netty.commons.handlers

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

/**
 * EchoHandlers
 *
 * @author tech@intellij.io
 */
class EchoHandler(
    val byteArrayConsumer: (ByteArray) -> Unit
) : SimpleChannelInboundHandler<ByteBuf>() {
    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: ByteBuf) {
        val bytes = ByteArray(msg.readableBytes())
        msg.readBytes(bytes)
        byteArrayConsumer(bytes)
        ctx.writeAndFlush(Unpooled.wrappedBuffer(bytes))
    }

}