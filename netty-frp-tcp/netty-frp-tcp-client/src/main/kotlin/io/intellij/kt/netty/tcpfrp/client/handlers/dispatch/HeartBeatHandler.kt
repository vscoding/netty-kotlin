package io.intellij.kt.netty.tcpfrp.client.handlers.dispatch

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.tcpfrp.protocol.channel.FrpChannel
import io.intellij.kt.netty.tcpfrp.protocol.channel.getFrpChannel
import io.intellij.kt.netty.tcpfrp.protocol.channel.initDispatchManager
import io.intellij.kt.netty.tcpfrp.protocol.heartbeat.Ping
import io.intellij.kt.netty.tcpfrp.protocol.heartbeat.Pong
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.util.AttributeKey
import io.netty.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * HeartBeatHandler
 *
 * 1. 初始化 [io.intellij.kt.netty.tcpfrp.protocol.channel.DispatchManager]
 * 2. 处理心跳，开启定时发送心跳的任务
 *
 * @author tech@intellij.io
 */
class HeartBeatHandler : SimpleChannelInboundHandler<Pong>() {

  companion object {
    private val log = getLogger(HeartBeatHandler::class.java)
    private val PING_TASK_FUTURE = AttributeKey.valueOf<ScheduledFuture<*>>("ping")
  }

  /**
   * Triggered from [io.intellij.kt.netty.tcpfrp.client.handlers.initial.ListeningResponseHandler]
   */
  @Throws(Exception::class)
  override fun channelActive(ctx: ChannelHandlerContext) {
    val ch = ctx.channel()
    val frpChannel: FrpChannel = ch.getFrpChannel()

    frpChannel.initDispatchManager()

    log.info("[channelActive]: Pong Handler. Start scheduled ping ...")
    // 5s ping
    ctx.channel().attr(PING_TASK_FUTURE).set(
      ctx.executor().scheduleAtFixedRate(
        {
          frpChannel.writeAndFlush(Ping.build("frp-client"))
        },
        1, 5, TimeUnit.SECONDS,
      ),
    )
    // must but just once
    frpChannel.activeRead()
  }

  @Throws(Exception::class)
  override fun channelRead0(ctx: ChannelHandlerContext, msg: Pong?) {
    log.info("HeatBeat PONG|{}", msg)
    ctx.channel().getFrpChannel().activeRead()
  }

  @Throws(Exception::class)
  override fun channelInactive(ctx: ChannelHandlerContext) {
    log.warn("stop scheduled ping ...")
    val scheduledFuture = ctx.channel().attr(PING_TASK_FUTURE).get()
    scheduledFuture.cancel(true)

    ctx.channel().getFrpChannel().close()

    super.channelInactive(ctx)
  }

}