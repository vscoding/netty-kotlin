package io.intellij.kt.netty.server.socks.handlers.connect

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.commons.utils.ChannelUtils.closeOnFlush
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

    @Throws(Exception::class)
    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
    }

    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        if (relayChannel.isActive) {
            relayChannel.writeAndFlush(msg)
        } else {
            ReferenceCountUtil.release(msg)
        }
    }

    @Throws(Exception::class)
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
