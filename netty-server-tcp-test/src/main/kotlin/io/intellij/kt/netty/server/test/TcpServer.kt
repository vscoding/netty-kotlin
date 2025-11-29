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

    private data class Config(
        val port: Int = 8080,
        val type: String = "echo" // "echo" or "log"
    )

    private fun resolveConfig(): Config {
        val port = System.getenv("SERVER_PORT")?.toIntOrNull() ?: 8080
        val type = System.getenv("TEST_TYPE")?.lowercase() ?: "log"
        return Config(port, type)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val config = resolveConfig()

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
                        .addLast(
                            when (config.type) {
                                "echo" -> EchoHandler()
                                "log" -> LogHandler()
                                else -> LogHandler()
                            }
                        )
                }
            })
        try {
            val sync = b.bind(config.port).sync()
            log.info("TcpServer (type:{}) start on port: {}", config.type, config.port)
            sync.channel().closeFuture().sync()
        } catch (e: Exception) {
            log.error("TcpServer start failed: ${e.message}", e)
        } finally {
            boss.shutdownGracefully()
            worker.shutdownGracefully()
        }

    }

}
