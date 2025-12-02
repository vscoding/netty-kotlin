package io.intellij.kt.netty.tcp.lb.handlers

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.commons.utils.ChannelUtils
import io.intellij.kt.netty.tcp.lb.config.Backend
import io.intellij.kt.netty.tcp.lb.config.LbStrategy
import io.intellij.kt.netty.tcp.lb.selector.BackendSelector
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.util.AttributeKey

/**
 * FrontendInboundHandler
 *
 * @author tech@intellij.io
 */
class FrontendInboundHandler(
    val strategy: LbStrategy,
    val backends: Map<String, Backend>
) : ChannelInboundHandlerAdapter() {

    companion object {
        private val log = getLogger(FrontendInboundHandler::class.java)
        val OUTBOUND_CHANNEL_KEY: AttributeKey<Channel> = AttributeKey.newInstance<Channel>("outboundChannel")
    }

    @Throws(Exception::class)
    override fun channelActive(ctx: ChannelHandlerContext) {
        val inboundChannel = ctx.channel()

        val chooser: BackendSelector = BackendSelector.get(strategy, backends)
        val loop = ClientConnector(chooser, inboundChannel)
        loop.connect()
    }

    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val inbound = ctx.channel()
        val outbound = inbound.attr(OUTBOUND_CHANNEL_KEY).get()
        if (outbound.isActive) {
            outbound.writeAndFlush(msg).addListener(
                ChannelFutureListener { future: ChannelFuture ->
                    if (future.isSuccess) {
                        // was able to flush outbound data, start to read the next chunk
                        // 切入点
                        inbound.read()
                    } else {
                        future.channel().close()
                    }
                }
            )
        }
    }

    @Throws(Exception::class)
    override fun channelInactive(ctx: ChannelHandlerContext) {
        // client closes the connection
        val outboundChannel = ctx.channel().attr<Channel?>(OUTBOUND_CHANNEL_KEY).get()
        ChannelUtils.closeOnFlush(outboundChannel)
    }

    @Throws(Exception::class)
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable?) {
        log.error("FrontendHandler error", cause)
        ChannelUtils.closeOnFlush(ctx.channel())
    }


}
