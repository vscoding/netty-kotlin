package io.intellij.kt.netty.tcpfrp.client.handlers.initial

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.commons.utils.ChannelUtils
import io.intellij.kt.netty.tcpfrp.client.handlers.dispatch.DispatchToServiceHandler
import io.intellij.kt.netty.tcpfrp.client.handlers.dispatch.ReceiveUserStateHandler
import io.intellij.kt.netty.tcpfrp.protocol.client.ListeningConfig
import io.intellij.kt.netty.tcpfrp.protocol.server.ListeningResponse
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

/**
 * ListeningResponseHandler
 *
 * @author tech@intellij.io
 */
class ListeningResponseHandler(val configMap: Map<String, ListeningConfig>) :
    SimpleChannelInboundHandler<ListeningResponse>() {

    companion object {
        private val log = getLogger(ListeningResponseHandler::class.java)
    }

    @Throws(Exception::class)
    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.read()
    }

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, listeningResponse: ListeningResponse) {
        if (listeningResponse.success) {
            log.info("listening request success")
            ctx.channel().writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener { channelFuture: ChannelFuture ->
                    if (channelFuture.isSuccess) {
                        val p = ctx.pipeline()
                        p.remove(this)
                        p.addLast(PongHandler())
                            .addLast(ReceiveUserStateHandler(configMap))
                            .addLast(DispatchToServiceHandler())

                        log.info("ListeningResponseHandler channelRead0|fireChannelActive")
                        p.fireChannelActive()
                    } else {
                        ChannelUtils.closeOnFlush(ctx.channel())
                    }
                })
        } else {
            val listeningStatus: Map<Int, Boolean> = listeningResponse.listeningStatus
            log.warn("listening request failure|{}", listeningStatus)
            ctx.close()
        }
    }
}
