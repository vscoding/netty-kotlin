package io.intellij.kt.netty.examples.idle

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.commons.utils.CtxUtils
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.timeout.IdleState
import io.netty.handler.timeout.IdleStateEvent

/**
 * TimeoutsHandler
 *
 * @author tech@intellij.io
 */
class TimeoutsHandler(
    val readerIdleTime: Long,
    val writerIdleTime: Long,
    val allIdleTime: Long
) : ChannelInboundHandlerAdapter() {
    companion object {
        private val log = getLogger(TimeoutsHandler::class.java)
    }

    @Throws(Exception::class)
    override fun channelActive(ctx: ChannelHandlerContext) {
        log.info("Client connected | remoteAddr={}", ctx.channel().remoteAddress())
    }

    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        ctx.write(msg)
    }

    @Throws(Exception::class)
    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.flush()
    }

    @Throws(Exception::class)
    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any) {
        if (evt is IdleStateEvent) {
            val address = CtxUtils.getRemoteAddress(ctx)
            when (evt.state()) {
                IdleState.READER_IDLE -> {
                    log.warn(
                        "No inbound data for {}s | remoteAddr={} | state={}",
                        readerIdleTime, address, evt.state()
                    )
                    // ctx.close()
                }

                IdleState.WRITER_IDLE -> log.warn(
                    "No outbound data for {}s | remoteAddr={} | state={}",
                    writerIdleTime, address, evt.state()
                )

                IdleState.ALL_IDLE -> log.warn(
                    "No inbound/outbound data for {}s | remoteAddr={} | state={}",
                    allIdleTime, address, evt.state()
                )
            }
        } else {
            super.userEventTriggered(ctx, evt)
        }
    }

}
