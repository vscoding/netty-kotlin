package io.intellij.kt.netty.server.socks.handlers.connect

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.commons.utils.ChannelUtils.closeOnFlush
import io.intellij.kt.netty.commons.utils.ConnInfo
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.util.AttributeKey
import io.netty.util.ReferenceCountUtil

enum class Direction {
    INBOUND, OUTBOUND
}

val INBOUND_CONN_INFO: AttributeKey<ConnInfo> = AttributeKey.valueOf("inbound_conn_info")
val OUTBOUND_CONN_INFO: AttributeKey<ConnInfo> = AttributeKey.valueOf("outbound_conn_info")

/**
 * RelayHandler
 *
 * @author tech@intellij.io
 */
class RelayHandler(
    private val relayChannel: Channel,
    private val direction: Direction,
) : ChannelInboundHandlerAdapter() {

    companion object {
        private val log = getLogger(RelayHandler::class.java)
    }

    @Throws(Exception::class)
    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
    }

    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (relayChannel.isActive) {
            relayChannel.writeAndFlush(msg)
        } else {
            ReferenceCountUtil.release(msg)
        }
    }

    @Throws(Exception::class)
    override fun channelInactive(ctx: ChannelHandlerContext) {
        when (direction) {
            Direction.INBOUND -> {
                ctx.channel().attr(INBOUND_CONN_INFO).also {
                    it.get()?.let { connInfo ->
                        log.info("Inbound channel closed, connection={}", connInfo)
                    }
                }.set(null)
            }

            Direction.OUTBOUND -> {
                ctx.channel().attr(OUTBOUND_CONN_INFO).also {
                    it.get()?.let { connInfo ->
                        log.info("Outbound channel closed, connection={}", connInfo)
                    }
                }.set(null)
            }
        }
        if (relayChannel.isActive) {
            closeOnFlush(relayChannel)
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        log.error("RelayHandler.exceptionCaught, direction={}, cause={}", direction, cause.message)
        ctx.close()
    }

}
