package io.intellij.kotlin.netty.server

import io.intellij.kotlin.netty.commons.getLogger
import io.intellij.kotlin.netty.commons.utils.CtxUtils
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler

/**
 * EchoServer
 *
 * @author tech@intellij.io
 */
class ServerChannelInitial : ChannelInitializer<SocketChannel>() {
    override fun initChannel(ch: SocketChannel) {
        ch.pipeline().addLast(EchoChannelHandler())
    }
}

class EchoChannelHandler : ChannelInboundHandlerAdapter() {
    private val log = getLogger(EchoChannelHandler::class.java)

    override fun channelActive(ctx: ChannelHandlerContext) {
        log.info("channel active: {}", CtxUtils.getRemoteAddress(ctx))
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        ctx.write(msg)
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.flush()
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        log.info("channel inactive: {}", CtxUtils.getRemoteAddress(ctx))
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        ctx.close()
        log.error("exceptionCaught: {}", cause.message)
    }
}

private val log = getLogger("EchoServer")

fun main() {
    val port = 8082
    val boss = NioEventLoopGroup(1)
    val worker = NioEventLoopGroup(4)
    try {
        ServerBootstrap().apply {
            group(boss, worker)
            channel(NioServerSocketChannel::class.java)
            handler(LoggingHandler(LogLevel.DEBUG))
            childHandler(ServerChannelInitial())
        }.also {
            val future = it.bind(port).sync()
            log.info("Echo server started on port: $port")
            future.channel().closeFuture().sync()
        }
    } catch (e: Exception) {
        log.error("Echo server error: {}", e.message)
    } finally {
        boss.shutdownGracefully()
        worker.shutdownGracefully()
    }

}