package io.intellij.kt.netty.server.socks

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.server.socks.Environment.PORT
import io.intellij.kt.netty.server.socks.handlers.socks5auth.Authenticator
import io.intellij.kt.netty.server.socks.handlers.socks5auth.PasswordAuthenticator
import io.netty.bootstrap.ServerBootstrap
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

        val b = ServerBootstrap().apply {
            channel(NioServerSocketChannel::class.java)
            handler(LoggingHandler(LogLevel.INFO))
            childHandler(SocksServerInitializer(authenticator))
        }

        try {
            val syncFuture = b.bind(PORT).sync()
            log.info("Socks server started on port {}", PORT)
            syncFuture.channel().closeFuture().sync()
        } catch (e: Exception) {
            log.error("Socks server start failed", e)
        } finally {
            boss.shutdownGracefully()
            worker.shutdownGracefully()
        }

    }

}