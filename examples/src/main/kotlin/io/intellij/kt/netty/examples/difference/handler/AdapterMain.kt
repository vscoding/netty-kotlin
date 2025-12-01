package io.intellij.kt.netty.examples.difference.handler

import io.intellij.kt.netty.commons.getLogger
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel

/**
 * AdapterMain
 *
 * ChannelInboundHandlerAdapter 的 ByteBuf 引用计数器不会主动 -1
 *
 * @author tech@intellij.io
 */
object AdapterMain {
    private val log = getLogger("AdapterHandler")

    @JvmStatic
    fun main(args: Array<String>) {

        val boss: EventLoopGroup = NioEventLoopGroup(1)
        val worker: EventLoopGroup = NioEventLoopGroup()

        val bootstrap = ServerBootstrap()
        try {
            bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(object : ChannelInitializer<SocketChannel>() {
                    @Throws(Exception::class)
                    override fun initChannel(ch: SocketChannel) {
                        ch.pipeline().addLast(object : ChannelInboundHandlerAdapter() {

                            @Throws(Exception::class)
                            override fun channelActive(ctx: ChannelHandlerContext) {
                                log.info("channel active")
                            }

                            @Throws(Exception::class)
                            override fun channelInactive(ctx: ChannelHandlerContext) {
                                log.info("channel inactive")
                            }

                            @Throws(Exception::class)
                            override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
                                if (msg is ByteBuf) {
                                    val i = msg.readableBytes()
                                    val bytes = ByteArray(i)
                                    msg.readBytes(bytes)

                                    log.info("当前的 ByteBuf 引用计数: {}", msg.refCnt())

                                    log.info("channel read: {}", String(bytes))
                                    msg.writeBytes(bytes)

                                    ctx.write(msg)
                                }
                            }

                            @Throws(Exception::class)
                            override fun channelReadComplete(ctx: ChannelHandlerContext) {
                                ctx.flush()
                            }

                            @Throws(Exception::class)
                            override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable) {
                                log.error("exception caught: {}", cause.message)
                            }
                        })
                    }
                })

            val f = bootstrap.bind(8081).sync()
            log.info("server start on port: 8081")
            f.channel().closeFuture().sync()
        } finally {
            boss.shutdownGracefully()
            worker.shutdownGracefully()
        }
    }

}