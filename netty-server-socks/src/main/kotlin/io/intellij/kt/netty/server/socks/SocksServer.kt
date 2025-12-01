package io.intellij.kt.netty.server.socks

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.server.socks.config.Properties.PORT
import io.intellij.kt.netty.server.socks.handlers.socks5auth.Authenticator
import io.intellij.kt.netty.server.socks.handlers.socks5auth.PasswordAuthenticator
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.nio.NioEventLoopGroup
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
        val boss = NioEventLoopGroup(1)
        val worker = NioEventLoopGroup()

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

}