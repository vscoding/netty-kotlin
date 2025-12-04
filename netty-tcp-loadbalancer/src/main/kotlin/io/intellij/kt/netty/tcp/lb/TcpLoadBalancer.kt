package io.intellij.kt.netty.tcp.lb

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.tcp.lb.config.ConfigParser
import io.intellij.kt.netty.tcp.lb.handlers.LoadBalancerInitializer
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.MultiThreadIoEventLoopGroup
import io.netty.channel.nio.NioIoHandler
import io.netty.channel.socket.nio.NioServerSocketChannel

/**
 * TcpLoadBalancer
 *
 * @author tech@intellij.io
 */
private val log = getLogger("TcpLoadBalancer")

fun main() {
    ConfigParser.loadConfig("lb-config.json")?.let {
        log.info("lbConfig: {}", it)

        val factory = NioIoHandler.newFactory()
        val boss = MultiThreadIoEventLoopGroup(1, factory)
        val worker = MultiThreadIoEventLoopGroup(factory)

        val bootstrap = ServerBootstrap()
            .group(boss, worker)
            .channel(NioServerSocketChannel::class.java)
            .childHandler(LoadBalancerInitializer(it.strategy, it.backends))
            .childOption(ChannelOption.AUTO_READ, false)

        runCatching {
            val future = bootstrap.bind(it.port).sync()
            log.info("TcpLoadBalancer start on port: {}", it.port)
            future.channel().closeFuture().sync()
        }.onFailure { e ->
            log.error("TcpLoadBalancer start error", e)
        }.also {
            boss.shutdownGracefully()
            worker.shutdownGracefully()
        }
    } ?: log.error("load config failed, exit")

}