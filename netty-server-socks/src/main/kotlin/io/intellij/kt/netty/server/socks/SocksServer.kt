package io.intellij.kt.netty.server.socks

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.server.socks.config.Properties.PORT
import io.intellij.kt.netty.server.socks.handlers.socks5auth.Authenticator
import io.intellij.kt.netty.server.socks.handlers.socks5auth.PasswordAuthenticator
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler

/**
 * SocksServer
 *
 * @author tech@intellij.io
 */
private val log = getLogger("SocksServer")

@Throws(Exception::class)
fun main() {
    val bossGroup: EventLoopGroup = NioEventLoopGroup(1)
    val workerGroup: EventLoopGroup = NioEventLoopGroup()

    val authenticator: Authenticator = PasswordAuthenticator()
    try {
        val b = ServerBootstrap()
        b.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel::class.java)
            .handler(LoggingHandler(LogLevel.INFO))
            .childHandler(SocksServerInitializer(authenticator))
        val sync: ChannelFuture = b.bind(PORT).sync()
        log.info("Socks server started on port {}", PORT)
        sync.channel().closeFuture().sync()
    } finally {
        bossGroup.shutdownGracefully()
        workerGroup.shutdownGracefully()
    }
}
