package io.intellij.kt.netty.spring.boot.services

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.commons.utils.ServerSocketUtils
import io.intellij.kt.netty.spring.boot.entities.NettyServerConf
import io.intellij.kt.netty.spring.boot.entities.ServerRunRes
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelHandler
import io.netty.channel.EventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import org.springframework.stereotype.Service
import java.util.Objects
import java.util.concurrent.ConcurrentHashMap

/**
 * NettyTcpServerService
 *
 * @author tech@intellij.io
 */
@Service
class NettyTcpServerService(
    private val bossGroup: EventLoopGroup,
    private val workerGroup: EventLoopGroup,
    private val channelHandlerMap: Map<String, ChannelHandler>
) : NettyServerService {

    companion object {
        private val log = getLogger(NettyTcpServerService::class.java)
    }

    private val runningChannelFutureMap: MutableMap<Int, ChannelFuture> = ConcurrentHashMap<Int, ChannelFuture>()

    @Synchronized
    override fun start(conf: NettyServerConf): ServerRunRes {
        val port: Int = conf.port
        if (runningChannelFutureMap.containsKey(port)) {
            log.warn("NettyTcpServer already running on port: {}", port)
            return ServerRunRes(false, "NettyTcpServer already running on port: $port")
        }

        if (ServerSocketUtils.isPortInUse(port)) {
            log.error("Port {} is already in use", port)
            return ServerRunRes(false, "Port $port is already in use")
        }

        val key: String? = conf.handlerKey
        if (key.isNullOrBlank()) {
            log.error("handlerKey is required")
            return ServerRunRes(false, "handlerKey is required")
        }

        val channelHandler = channelHandlerMap[key]

        if (Objects.isNull(channelHandler)) {
            log.error("ChannelHandler not found for key: {}", key)
            return ServerRunRes(false, "ChannelHandler not found for key: $key")
        }

        val bootstrap = ServerBootstrap()
        bootstrap.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel::class.java)
            .childHandler(channelHandler)
        try {
            val bind = bootstrap.bind(port)
            bind.addListener(GenericFutureListener { future: Future<in Void> ->
                if (future.isSuccess) {
                    log.info("NettyTcpServer(key={}) started on port: {}", key, port)
                } else {
                    log.error("NettyTcpServer(key={}) start failed", key, future.cause())
                }
            })
            val channelFuture = bind.sync()
            runningChannelFutureMap[port] = channelFuture!!
            return ServerRunRes(true, "NettyTcpServer(key={$key}) started on port: $port")
        } catch (e: Exception) {
            log.error("NettyTcpServer(key={}) start failed", key, e)
            return ServerRunRes(false, "NettyTcpServer(key={$key}) start failed: ${e.message}")
        }
    }

    @Synchronized
    override fun isRunning(port: Int): Boolean {
        return runningChannelFutureMap.containsKey(port)
    }

    @Synchronized
    override fun stop(port: Int) {
        val future = runningChannelFutureMap[port]
        future?.also {
            try {
                it.channel().close().sync()
            } catch (e: InterruptedException) {
                log.error("NettyTcpServer stop failed", e)
            } finally {
                runningChannelFutureMap.remove(port)
            }
        } ?: {
            log.warn("NettyTcpServer not running on port: {}", port)
        }
    }

    @Synchronized
    override fun stopAll() {
        runningChannelFutureMap.forEach { (port, future) ->
            try {
                log.info("NettyTcpServer(port={}) stopping...", port)
                future.channel().close().sync()
            } catch (e: InterruptedException) {
                log.error("NettyTcpServer stop failed", e)
            } finally {
                runningChannelFutureMap.remove(port)
            }
        }
        // bossGroup.shutdownGracefully()
        // workerGroup.shutdownGracefully()
    }

}