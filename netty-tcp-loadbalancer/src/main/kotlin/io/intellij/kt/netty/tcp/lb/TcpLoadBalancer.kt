package io.intellij.kt.netty.tcp.lb

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.tcp.lb.config.ConfigParser
import io.intellij.kt.netty.tcp.lb.handlers.LoadBalancerInitializer
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel

/**
 * TcpLoadBalancer
 *
 * @author tech@intellij.io
 */

private val log = getLogger("TcpLoadBalancer")

fun main() {
    ConfigParser.loadConfig("lb-config.json")?.also {
        log.info("lbConfig: {}", it)
        val boss = NioEventLoopGroup(1)
        val worker = NioEventLoopGroup()
        val bootstrap = ServerBootstrap()

        try {
            bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(LoadBalancerInitializer(it.strategy, it.backends))
                .childOption(ChannelOption.AUTO_READ, false)
            val sync: ChannelFuture = bootstrap.bind(it.port).sync()
            log.info("TcpLoadBalancer start on port: {}", it.port)
            sync.channel().closeFuture().sync()
        } catch (e: Exception) {
            log.error("TcpLoadBalancer start error | {}", e.message)
        } finally {
            boss.shutdownGracefully()
            worker.shutdownGracefully()
        }
    } ?: run {
        // 加载配置失败时打印
        log.error("load config failed, exit")
    }
}