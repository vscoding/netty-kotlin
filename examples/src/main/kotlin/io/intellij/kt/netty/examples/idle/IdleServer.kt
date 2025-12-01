package io.intellij.kt.netty.examples.idle

import io.intellij.kt.netty.commons.getLogger
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.timeout.IdleStateEvent
import io.netty.handler.timeout.IdleStateHandler
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import java.util.concurrent.TimeUnit

/**
 * IdleServer
 *
 * @author tech@intellij.io
 */
object IdleServer {
    private val log = getLogger(IdleServer::class.java)
    var port: Int = 7000

    @JvmStatic
    @Throws(Exception::class)
    fun main(args: Array<String>) {
        val boss: EventLoopGroup = NioEventLoopGroup(1)
        val worker: EventLoopGroup = NioEventLoopGroup(2)

        val bootstrap = ServerBootstrap()

        try {
            bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(object : ChannelInitializer<SocketChannel>() {
                    @Throws(Exception::class)
                    override fun initChannel(ch: SocketChannel) {
                        val p = ch.pipeline()

                        val readIdleTime = 3 // 读空闲时间，单位秒

                        p.addLast(IdleStateHandler(readIdleTime.toLong(), 0, 0, TimeUnit.SECONDS))
                        p.addLast(object : ChannelInboundHandlerAdapter() {
                            @Throws(Exception::class)
                            override fun channelActive(ctx: ChannelHandlerContext) {
                                log.info("接收到客户端连接|channel.remoteAddr={}", ctx.channel().remoteAddress())
                            }

                            @Throws(Exception::class)
                            override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any) {
                                if (evt is IdleStateEvent) {
                                    // 读超时，关闭连接
                                    log.error(
                                        "客户端连接{}秒内没有数据，关闭连接|channel.remoteAddr={}",
                                        readIdleTime,
                                        ctx.channel().remoteAddress()
                                    )
                                    ctx.close()
                                } else {
                                    super.userEventTriggered(ctx, evt)
                                }
                            }
                        })
                    }
                })

            val bind = bootstrap.bind(port).sync()
            bind.addListener(GenericFutureListener { future: Future<in Void> ->
                if (future.isSuccess) {
                    log.info("Idle Server Started successfully on port {}", port)
                } else {
                    log.error("Idle Server Started failed on port {}", port, future.cause())
                }
            })

            bind.channel().closeFuture().sync()
        } finally {
            boss.shutdownGracefully()
            worker.shutdownGracefully()
        }
    }

}