package io.intellij.kt.netty.examples.idle

import io.intellij.kt.netty.commons.getLogger
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.MultiThreadIoEventLoopGroup
import io.netty.channel.nio.NioIoHandler
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.timeout.IdleStateHandler
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import java.util.concurrent.TimeUnit

/**
 * IdleServer
 *
 * @author tech@intellij.io
 */
object TimeoutsServer {
  private val log = getLogger(TimeoutsServer::class.java)

  const val PORT: Int = 7000

  @JvmStatic
  @Throws(Exception::class)
  fun main(args: Array<String>) {
    val f = NioIoHandler.newFactory()
    val boss: EventLoopGroup = MultiThreadIoEventLoopGroup(1, f)
    val worker: EventLoopGroup = MultiThreadIoEventLoopGroup(2, f)

    val bootstrap = ServerBootstrap()

    val readerIdleTime = 3L // 读空闲时间，单位秒
    val writerIdleTime = 3L
    val allIdleTime = 5L

    try {
      bootstrap.group(boss, worker)
        .channel(NioServerSocketChannel::class.java)
        .childHandler(
          object : ChannelInitializer<SocketChannel>() {
            @Throws(Exception::class)
            override fun initChannel(ch: SocketChannel) {
              val p = ch.pipeline()
              p.addLast(IdleStateHandler(readerIdleTime, writerIdleTime, allIdleTime, TimeUnit.SECONDS))
              p.addLast(TimeoutsHandler(readerIdleTime, writerIdleTime, allIdleTime))
            }
          },
        )

      val bind = bootstrap.bind(PORT).sync()
      bind.addListener(
        GenericFutureListener { future: Future<in Void> ->
          if (future.isSuccess) {
            log.info("Idle Server Started successfully on port {}", PORT)
          } else {
            log.error("Idle Server Started failed on port {}", PORT, future.cause())
          }
        },
      )

      bind.channel().closeFuture().sync()
    } finally {
      boss.shutdownGracefully()
      worker.shutdownGracefully()
    }
  }

}