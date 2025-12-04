package io.intellij.kt.netty.server.socks

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.server.socks.SocksServer.Environment.PORT
import io.intellij.kt.netty.server.socks.handlers.socks5auth.Authenticator
import io.intellij.kt.netty.server.socks.handlers.socks5auth.PasswordAuthenticator
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.EventLoopGroup
import io.netty.channel.MultiThreadIoEventLoopGroup
import io.netty.channel.nio.NioIoHandler
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler

/**
 * SocksServer
 *
 * @author tech@intellij.io
 */
object SocksServer {
    private val log = getLogger(SocksServer::class.java)

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val factory = NioIoHandler.newFactory()
        val boss: EventLoopGroup = MultiThreadIoEventLoopGroup(1, factory)
        val worker: EventLoopGroup = MultiThreadIoEventLoopGroup(factory)

        val authenticator: Authenticator = PasswordAuthenticator()

        val b = ServerBootstrap()
        b.group(boss, worker)
            .channel(NioServerSocketChannel::class.java)
            .handler(LoggingHandler(LogLevel.INFO))
            .childHandler(SocksServerInitializer(authenticator))

        runCatching {
            val sync: ChannelFuture = b.bind(PORT).sync()
            log.info("Socks server started on port {}", PORT)
            sync.channel().closeFuture().sync()
        }.onFailure { e ->
            log.error("Socks server start failed", e)
        }.also {
            boss.shutdownGracefully()
            worker.shutdownGracefully()
        }
    }


    /**
     * The `Environment` object provides configuration settings for the server,
     * such as environment variables and port settings. It utilizes system
     * environment variables for initialization and logs relevant information
     * or errors about the configuration during its setup.
     *
     * Primary Features:
     * - Initializes the server's port (`PORT`) from the environment variable `SERVER_PORT`
     *   or defaults to a predefined value if not provided.
     * - Retrieves SOCKS5 credentials (`SOCKS5_USERNAME` and `SOCKS5_PASSWORD`)
     *   from environment variables.
     * - Logs configuration events, such as missing or invalid environment settings.
     */
    object Environment {
        private val log = getLogger(Environment::class.java)

        var PORT: Int = 1080

        val SOCKS5_USERNAME: String = System.getenv("SOCKS5_USERNAME") ?: ""
        val SOCKS5_PASSWORD: String = System.getenv("SOCKS5_PASSWORD") ?: ""


        init {
            val portStr = System.getenv("SERVER_PORT")
            if (portStr == null || portStr.isEmpty()) {
                log.info("SERVER_PORT is not set, use default port: {}", PORT)
            } else {
                runCatching {
                    PORT = portStr.toInt()
                }.onFailure {
                    log.error("SERVER_PORT is invalid: {}", portStr)
                }
            }
        }

    }
}