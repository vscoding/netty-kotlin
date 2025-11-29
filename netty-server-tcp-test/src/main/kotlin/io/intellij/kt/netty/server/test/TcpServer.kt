package io.intellij.kt.netty.server.test

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.server.test.handlers.ConnectionHandler
import io.intellij.kt.netty.server.test.handlers.EchoHandler
import io.intellij.kt.netty.server.test.handlers.LogHandler
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel

/**
 * TcpServer
 *
 * @author tech@intellij.io
 */
object TcpServer {
    private val log = getLogger(TcpServer::class.java)

    private val port = System.getProperty("port")?.toInt() ?: 8080
    private val type = System.getProperty("type") ?: "echo"

    @JvmStatic
    fun main(args: Array<String>) {
        val handler = when (type) {
            "echo" -> EchoHandler()
            "log" -> LogHandler()
            else -> {
                log.error("Unknown server type: $type")
                return
            }
        }

        val boss = NioEventLoopGroup(1)
        val worker = NioEventLoopGroup()

        val b = ServerBootstrap()

        b.group(boss, worker)
            .channel(NioServerSocketChannel::class.java)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .childHandler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(ch: SocketChannel) {
                    ch.pipeline()
                        .addLast(ConnectionHandler())
                        .addLast(handler)
                }
            })
        try {
            val sync = b.bind(port).sync()
            log.info("TcpServer (type:{}) start on port: {}", type, port)
            sync.channel().closeFuture().sync()
        } catch (e: Exception) {
            log.error("TcpServer start failed: ${e.message}", e)
        } finally {
            boss.shutdownGracefully()
            worker.shutdownGracefully()
        }

    }

}
