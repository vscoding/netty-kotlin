package io.intellij.kt.netty.server

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.commons.utils.CtxUtils
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel

/**
 * ServerChannelInitial
 *
 * @author tech@intellij.io
 */
class ServerChannelInitial : ChannelInitializer<SocketChannel>() {
    @Throws(Exception::class)
    override fun initChannel(ch: SocketChannel) {
        ch.pipeline().addLast(EchoChannelHandler())
    }
}

class EchoChannelHandler : ChannelInboundHandlerAdapter() {
    companion object {
        private val log = getLogger(EchoChannelHandler::class.java)
    }

    @Throws(Exception::class)
    override fun channelActive(ctx: ChannelHandlerContext) {
        log.info("channel active: {}", CtxUtils.getRemoteAddress(ctx))
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
    override fun channelInactive(ctx: ChannelHandlerContext) {
        log.info("channel inactive: {}", CtxUtils.getRemoteAddress(ctx))
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        ctx.close()
        log.error("exceptionCaught: {}", cause.message)
    }
}


