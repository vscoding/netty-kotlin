package io.intellij.kt.netty.examples.idle

import io.intellij.kt.netty.commons.getLogger
import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener

/**
 * IdleClient
 *
 * @author tech@intellij.io
 */
object IdleClient {
    private val log = getLogger(IdleClient::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        val group: EventLoopGroup = NioEventLoopGroup(2)

        val bootstrap = Bootstrap()
        try {
            bootstrap.group(group).channel(NioSocketChannel::class.java)
                .handler(object : ChannelInitializer<SocketChannel>() {
                    @Throws(Exception::class)
                    override fun initChannel(ch: SocketChannel) {
                        ch.pipeline().addLast(object : ChannelInboundHandlerAdapter() {
                            @Throws(Exception::class)
                            override fun channelInactive(ctx: ChannelHandlerContext) {
                                log.error("服务端断开了连接|channel.remoteAddr={}", ctx.channel().remoteAddress())
                            }

                            @Throws(Exception::class)
                            override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
                                log.error("IdleClient exceptionCaught|{}", cause.message)
                                ctx.close()
                            }
                        })
                    }
                })

            val connect = bootstrap.connect("127.0.0.1", IdleServer.port)
            connect.addListener(GenericFutureListener { future: Future<in Void> ->
                if (future.isSuccess) {
                    log.info("连接服务端成功|channel.remoteAddr={}", connect.channel().remoteAddress())
                } else {
                    log.error("连接服务端失败|channel.remoteAddr={}", connect.channel().remoteAddress())
                }
            })

            connect.channel().closeFuture().sync()
        } finally {
            group.shutdownGracefully()
        }
    }

}