package io.intellij.kt.netty.tcp.lb

import com.alibaba.fastjson2.JSON
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.MultiThreadIoEventLoopGroup
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.nio.NioIoHandler
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpHeaderValues
import io.netty.handler.codec.http.HttpObject
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.codec.http.HttpVersion
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * MultiPortHttpServerTest
 *
 * @author tech@intellij.io
 */
class MultiPortHttpServerTest {

    @Test
    fun running() {
        val ports = listOf(8081, 8082, 8083)

        val factory = NioIoHandler.newFactory()
        val boss = MultiThreadIoEventLoopGroup(1, factory)
        val worker = MultiThreadIoEventLoopGroup(factory)

        val lock = ReentrantLock()
        val shutdownCondition = lock.newCondition() // Condition to signal shutdown

        runCatching {
            for (port in ports) {
                startServer(port, boss, worker, lock, shutdownCondition)
            }
            lock.lock()
            run {
                shutdownCondition.await()
            }.also {
                lock.unlock()
            }

        }.onFailure { e ->
            println("start server error: ${e.message}")
        }.also {
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
            .childHandler(
                object : ChannelInitializer<SocketChannel>() {
                    @Throws(Exception::class)
                    override fun initChannel(ch: SocketChannel) {
                        ch.pipeline()
                            .addLast(HttpServerCodec())
                            .addLast(HttpResponseHandler(port, lock, condition))
                    }
                }
            )
        println("Http Server start on port: $port")
        b.bind(port).sync()
    }

    class HttpResponseHandler(
        val port: Int,
        val lock: Lock,
        val shutdownCondition: Condition
    ) : SimpleChannelInboundHandler<HttpObject>() {

        companion object {
            val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        }

        @Throws(Exception::class)
        override fun channelRead0(ctx: ChannelHandlerContext, httpObject: HttpObject) {
            if (httpObject is HttpRequest) {
                val msg = JSON.toJSONString(
                    mapOf(
                        "time" to LocalDateTime.now().format(TIME_FORMATTER),
                        "port" to port
                    )
                )

                val bytes = msg.toByteArray()
                val response: FullHttpResponse = DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(bytes)
                )
                response.headers()
                    .set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                    .set(HttpHeaderNames.CONTENT_LENGTH, bytes.size.toString())

                ctx.write(response)
                    .addListener(ChannelFutureListener.CLOSE)

                // 获取 req 的uri
                val uri = httpObject.uri()
                if ("/shutdown" == uri) {
                    lock.lock()
                    try {
                        shutdownCondition.signalAll()
                    } finally {
                        lock.unlock()
                    }
                }
            }
        }

        @Throws(Exception::class)
        override fun channelReadComplete(ctx: ChannelHandlerContext) {
            ctx.flush()
        }
    }
}