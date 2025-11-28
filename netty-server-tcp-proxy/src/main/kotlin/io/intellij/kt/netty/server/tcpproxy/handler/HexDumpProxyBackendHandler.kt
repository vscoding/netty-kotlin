package io.intellij.kt.netty.server.tcpproxy.handler

import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

/**
 * HexDumpProxyBackendHandler
 *
 * @author tech@intellij.io
 */
class HexDumpProxyBackendHandler(
    val inboundChannel: Channel
) : ChannelInboundHandlerAdapter() {

    @Throws(Exception::class)
    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.read()
    }

    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        inboundChannel.writeAndFlush(msg).addListener(
            ChannelFutureListener { future: ChannelFuture ->
                if (future.isSuccess) {
                    ctx.channel().read()
                } else {
                    future.channel().close()
                }
            }
        )
    }

    @Throws(Exception::class)
    override fun channelInactive(ctx: ChannelHandlerContext) {
        HexDumpProxyFrontendHandler.closeOnFlush(inboundChannel)
    }

    @Throws(Exception::class)
    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable) {
        // cause.printStackTrace();
        HexDumpProxyFrontendHandler.closeOnFlush(inboundChannel)
    }

}
