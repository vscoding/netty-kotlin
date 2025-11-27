package io.intellij.netty.server.socks.handlers.connect

import io.intellij.kotlin.netty.commons.getLogger
import io.intellij.kotlin.netty.commons.utils.ChannelUtils.closeOnFlush
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.util.ReferenceCountUtil

/**
 * RelayHandler
 *
 * @author tech@intellij.io
 */
class RelayHandler(val relayChannel: Channel) : ChannelInboundHandlerAdapter() {
    companion object {
        private val log = getLogger(RelayHandler::class.java)
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
    }

    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        if (relayChannel.isActive) {
            relayChannel.writeAndFlush(msg)
        } else {
            ReferenceCountUtil.release(msg)
        }
    }

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        if (relayChannel.isActive) {
            closeOnFlush(relayChannel)
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable?) {
        // cause.printStackTrace();
        log.error("RelayHandler.exceptionCaught", cause)
        ctx.close()
    }
}
