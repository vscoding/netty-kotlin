package io.intellij.kt.netty.examples.difference.handler

import io.intellij.kt.netty.commons.getLogger
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.util.ReferenceCountUtil

/**
 * SimpleMain
 *
 * SimpleChannelInboundHandler 的 ByteBuf 引用计数器会主动 -1 (默认的)
 *
 * @author tech@intellij.io
 */
object SimpleMain {
    private val log = getLogger("SimpleHandler")

    @Throws(Exception::class)
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
                        ch.pipeline().addLast(object : SimpleChannelInboundHandler<ByteBuf>() {

                            @Throws(Exception::class)
                            override fun channelActive(ctx: ChannelHandlerContext) {
                                log.info("channel active")
                            }

                            @Throws(Exception::class)
                            override fun channelRead0(ctx: ChannelHandlerContext, msg: ByteBuf) {
                                val i = msg.readableBytes()
                                val bytes = ByteArray(i)
                                msg.readBytes(bytes)
                                log.info("receive msg: {}", String(bytes))

                                log.info("当前的 ByteBuf 引用计数: {}", msg.refCnt())

                                ReferenceCountUtil.retain(msg)
                                msg.writeBytes(bytes)
                                ctx.write(msg)
                            }

                            @Throws(Exception::class)
                            override fun channelReadComplete(ctx: ChannelHandlerContext) {
                                ctx.flush()
                            }

                            @Throws(Exception::class)
                            override fun channelInactive(ctx: ChannelHandlerContext?) {
                                log.warn("channel inactive")
                            }

                            @Throws(Exception::class)
                            override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
                                log.error("exception caught", cause)
                            }

                        })
                    }
                })

            val f = bootstrap.bind(8080).sync()
            log.info("server start on port: 8080")
            f.channel().closeFuture().sync()
        } finally {
            boss.shutdownGracefully()
            worker.shutdownGracefully()
        }
    }

}