package io.intellij.kt.netty.tcp.lb

import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.string.StringEncoder
import io.netty.util.ReferenceCountUtil
import org.junit.jupiter.api.Test
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * MultiPortEchoServerTest
 *
 * @author tech@intellij.io
 */
class MultiPortEchoServerTest {

    @Test
    @Throws(Exception::class)
    fun running() {
        val ports: List<Int> = listOf(8081, 8082, 8083)

        val boss = NioEventLoopGroup(1)
        val worker = NioEventLoopGroup(2)

        val lock = ReentrantLock()
        val shutdownCondition = lock.newCondition()

        try {
            for (port in ports) {
                startServer(port, boss, worker, lock, shutdownCondition)
            }
            lock.lock()
            try {
                // curl localhost:8081/shutdown stop all server
                shutdownCondition.await()
            } finally {
                lock.unlock()
            }
        } finally {
            boss.shutdownGracefully()
            worker.shutdownGracefully()
        }
    }

    @Throws(Exception::class)
    fun startServer(
        port: Int, boss: EventLoopGroup, worker: EventLoopGroup,
        lock: Lock, condition: Condition
    ) {
        val b = ServerBootstrap()

        b.group(boss, worker)
            .channel(NioServerSocketChannel::class.java)
            .childHandler(object : ChannelInitializer<SocketChannel>() {
                @Throws(Exception::class)
                override fun initChannel(ch: SocketChannel) {
                    ch.pipeline()
                        .addLast(StringEncoder())
                        .addLast(EchoResponseHandler(port, lock, condition))
                }
            })

        println("Echo Server start on port: $port")
        b.bind(port).sync()
    }

    class EchoResponseHandler(
        val port: Int,
        val lock: Lock,
        val shutdownCondition: Condition,
    ) : ChannelInboundHandlerAdapter() {

        @Throws(Exception::class)
        override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
            if (msg is ByteBuf) {
                val i = msg.readableBytes()
                val bytes = ByteArray(i)
                msg.readBytes(bytes)

                val response = String(bytes)

                val future = ctx.writeAndFlush("$response-$port")
                if ("shutdown" == response) {
                    future.addListener(ChannelFutureListener.CLOSE)
                    lock.lock()
                    try {
                        shutdownCondition.signalAll()
                    } finally {
                        lock.unlock()
                    }
                }
            }
            ReferenceCountUtil.release(msg)
        }

        @Throws(Exception::class)
        override fun channelInactive(ctx: ChannelHandlerContext) {
            println("Channel inactive: " + ctx.channel().remoteAddress())
        }
    }

}