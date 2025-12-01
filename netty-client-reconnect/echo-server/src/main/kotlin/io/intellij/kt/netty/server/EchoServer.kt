package io.intellij.kt.netty.server

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.commons.handlers.ActiveHandler
import io.intellij.kt.netty.commons.handlers.DirectEchoHandler
import io.netty.bootstrap.ServerBootstrap
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
object EchoServer {
    private val log = getLogger(EchoServer::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        val port = 8082
        val boss = NioEventLoopGroup(1)
        val worker = NioEventLoopGroup(4)
        try {
            ServerBootstrap().apply {
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

}