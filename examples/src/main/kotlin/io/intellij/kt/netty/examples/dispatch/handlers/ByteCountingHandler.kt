package io.intellij.kt.netty.examples.dispatch.handlers

import io.intellij.kt.netty.commons.getLogger
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

/**
 * ByteCountingHandler
 *
 * @author tech@intellij.io
 */
class ByteCountingHandler : ChannelInboundHandlerAdapter() {
    companion object {
        private val log = getLogger(ByteCountingHandler::class.java)
    }

    private var totalBytesReceived: Long = 0

    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg is ByteBuf) {
            totalBytesReceived += msg.readableBytes().toLong()
        }

        // 调用下一个处理器
        super.channelRead(ctx, msg)
    }

    @Throws(Exception::class)
    override fun channelInactive(ctx: ChannelHandlerContext) {
        // 打印总的接收到的字节数
        log.info("Total bytes received: {}", totalBytesReceived)
        super.channelInactive(ctx)
    }
}
