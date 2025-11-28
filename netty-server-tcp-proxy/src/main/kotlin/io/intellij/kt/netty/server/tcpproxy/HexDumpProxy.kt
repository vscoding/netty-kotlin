package io.intellij.kt.netty.server.tcpproxy

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.server.tcpproxy.handler.HexDumpProxyInitializer
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import java.io.File
import java.io.FileInputStream
import java.util.Properties

/**
 * HexDumpProxy
 *
 * @author tech@intellij.io
 */

val log = getLogger("HexDumpProxy")

data class TcpProxyConfig(
    val localHost: String,
    val localPort: Int,
    val remoteHost: String,
    val remotePort: Int
)

@Throws(Exception::class)
fun main() {
    val config = readProperties()
    log.info("config:{}", config)

    log.info("Proxying *:{} to {}:{}", config.localPort, config.remoteHost, config.remotePort)

    val boosGroup: EventLoopGroup = NioEventLoopGroup(1)
    val workerGroup: EventLoopGroup = NioEventLoopGroup()

    try {
        val b = ServerBootstrap()

        b.group(boosGroup, workerGroup)
            .channel(NioServerSocketChannel::class.java)
            .handler(LoggingHandler(LogLevel.INFO))
            .childHandler(HexDumpProxyInitializer(config.remoteHost, config.remotePort))
            .childOption(ChannelOption.AUTO_READ, false)
            .bind(config.localPort).sync().channel().closeFuture().sync()
    } finally {
        boosGroup.shutdownGracefully()
        workerGroup.shutdownGracefully()
    }
}

@Throws(Exception::class)
fun readProperties(): TcpProxyConfig {
    return if (File("config.properties").exists()) {
        log.info("read config file")

        val p = Properties()
        p.load(FileInputStream("config.properties"))
        val localHost = p.getProperty("localHost", "127.0.0.1")
        val localPort = p.getProperty("localPort", "3306").toInt()
        val remoteHost = p.getProperty("remoteHost", "172.100.1.100")
        val remotePort = p.getProperty("remotePort", "3306").toInt()
        TcpProxyConfig(localHost, localPort, remoteHost, remotePort)
    } else {
        val localHost = System.getProperty("localHost", "0.0.0.0")
        val localPort = System.getProperty("localPort", "3306").toInt()
        val remoteHost = System.getProperty("remoteHost", "dev1.iproute.org")
        val remotePort = System.getProperty("remotePort", "3306").toInt()

        TcpProxyConfig(localHost, localPort, remoteHost, remotePort)
    }
}



