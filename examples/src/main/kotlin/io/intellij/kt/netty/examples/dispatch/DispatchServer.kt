package io.intellij.kt.netty.examples.dispatch

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.examples.dispatch.initializer.ServerInitializer
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel

/**
 * DispatchServer
 *
 * @author tech@intellij.io
 */
object DispatchServer {
    private val log = getLogger(DispatchServer::class.java)

    const val PORT: Int = 7000

    @Throws(InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val bossGroup: EventLoopGroup = NioEventLoopGroup(1)
        val workerGroup: EventLoopGroup = NioEventLoopGroup()

        try {
            val b = ServerBootstrap()
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(ServerInitializer())
            val channel = b.bind(PORT).channel()
            log.info("server started at port {}", PORT)
            channel.closeFuture().sync()
        } finally {
            bossGroup.shutdownGracefully()
            workerGroup.shutdownGracefully()
        }
    }

}