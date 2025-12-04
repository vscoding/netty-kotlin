package io.intellij.kt.netty.examples.dispatch

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.examples.dispatch.initializer.ClientInitializer
import io.intellij.kt.netty.examples.dispatch.model.DataBody
import io.intellij.kt.netty.examples.dispatch.model.HeartBeat
import io.intellij.kt.netty.examples.dispatch.model.msg.LoginReq
import io.intellij.kt.netty.examples.dispatch.model.msg.LogoutReq
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener
import io.netty.channel.EventLoopGroup
import io.netty.channel.MultiThreadIoEventLoopGroup
import io.netty.channel.nio.NioIoHandler
import io.netty.channel.socket.nio.NioSocketChannel
import java.util.Date
import java.util.Random
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * DispatchClient
 *
 * @author tech@intellij.io
 */
class DispatchClient(
    private val host: String,
    private val port: Int
) {

    companion object {
        private val log = getLogger(DispatchClient::class.java)

        fun getRandomLongInRange(min: Long, max: Long): Long {
            val random = Random()
            return min + (random.nextDouble() * (max - min)).toLong()
        }

        @JvmStatic
        fun main(args: Array<String>) {
            val dispatchClient = DispatchClient("127.0.0.1", DispatchServer.PORT)
            if (dispatchClient.start()) {
                val ses = Executors.newScheduledThreadPool(3)
                ses.scheduleAtFixedRate({
                    dispatchClient.send(
                        HeartBeat(Date(), "client", getRandomLongInRange(1, 1000))
                    )
                }, 0, 3, TimeUnit.SECONDS)

                ses.scheduleAtFixedRate({
                    val loginReq = LoginReq("admin", "admin")
                    dispatchClient.send(loginReq)

                    val logoutDataBody: DataBody = LogoutReq.create("admin")
                    dispatchClient.send(logoutDataBody)

                }, 0, 5, TimeUnit.SECONDS)

                ses.schedule({
                    dispatchClient.stop()
                    ses.shutdown()
                }, 15, TimeUnit.SECONDS)
            }
        }

    }

    private val group: EventLoopGroup = MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory())
    private val bootstrap = Bootstrap()

    @Volatile
    private var channel: Channel? = null

    fun start(): Boolean {
        val future = bootstrap.group(group)
            .channel(NioSocketChannel::class.java)
            .handler(ClientInitializer())
            .connect(host, port)
            .addListener(ChannelFutureListener { channelFuture: ChannelFuture ->
                if (channelFuture.isSuccess) {
                    channel = channelFuture.channel()
                } else {
                    log.error("connect failed |{}", channelFuture.cause().message)
                    stop()
                }
            })

        try {
            future.sync()
            log.info("client started|connect to {}:{}", host, port)
            return true
        } catch (e: InterruptedException) {
            return false
        }
    }

    fun stop() {
        group.shutdownGracefully()
    }

    fun <T> send(msg: T) {
        channel?.also {
            if (it.isActive) {
                it.writeAndFlush(msg)
            }
        }
    }

}

