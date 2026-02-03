package io.intellij.kt.netty.server

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.commons.handlers.ActiveHandler
import io.intellij.kt.netty.commons.handlers.DirectEchoHandler
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.MultiThreadIoEventLoopGroup
import io.netty.channel.nio.NioIoHandler
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler

/**
 * EchoServer
 *
 * @author tech@intellij.io
 */
object EchoServer {
    private val log = getLogger(EchoServer::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        val port = 8082
        val ioHandlerFactory = NioIoHandler.newFactory()
        val boss = MultiThreadIoEventLoopGroup(1, ioHandlerFactory)
        val worker = MultiThreadIoEventLoopGroup(4, ioHandlerFactory)
        try {
            val bootstrap = ServerBootstrap().apply {
                group(boss, worker)
                channel(NioServerSocketChannel::class.java)
                handler(LoggingHandler(LogLevel.DEBUG))
                childHandler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel) {
                        ch.pipeline()
                            .addLast(ActiveHandler())
                            .addLast(DirectEchoHandler())
                    }
                })
            }

            val future = bootstrap.bind(port).sync()
            log.info("Echo server started on port: $port")
            future.channel().closeFuture().sync()

        } catch (e: Exception) {
            log.error("Echo server error: {}", e.message)
        } finally {
            boss.shutdownGracefully()
            worker.shutdownGracefully()
        }
    }

}