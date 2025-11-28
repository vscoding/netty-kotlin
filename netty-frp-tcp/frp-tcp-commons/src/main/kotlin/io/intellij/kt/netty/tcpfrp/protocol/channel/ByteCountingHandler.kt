package io.intellij.kt.netty.tcpfrp.protocol.channel

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

    private var tolal: Long = 0

    @Throws(Exception::class)
    override fun channelActive(ctx: ChannelHandlerContext) {
        // must
        ctx.read()
        super.channelActive(ctx)
    }

    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg is ByteBuf) {
            tolal += msg.readableBytes().toLong()
        }
        super.channelRead(ctx, msg)
    }

    @Throws(Exception::class)
    override fun channelInactive(ctx: ChannelHandlerContext) {
        log.info("byte counting|id={}|received={}B", DispatchIdUtils.getDispatchId(ctx.channel()), tolal)
        super.channelInactive(ctx)
    }
}
