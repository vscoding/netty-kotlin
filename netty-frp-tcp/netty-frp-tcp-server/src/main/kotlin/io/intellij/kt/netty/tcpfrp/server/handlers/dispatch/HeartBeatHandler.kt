package io.intellij.kt.netty.tcpfrp.server.handlers.dispatch

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.tcpfrp.protocol.channel.FrpChannel
import io.intellij.kt.netty.tcpfrp.protocol.channel.getDispatchManager
import io.intellij.kt.netty.tcpfrp.protocol.channel.getFrpChannel
import io.intellij.kt.netty.tcpfrp.protocol.channel.initDispatchManager
import io.intellij.kt.netty.tcpfrp.protocol.heartbeat.Ping
import io.intellij.kt.netty.tcpfrp.protocol.heartbeat.Pong
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

/**
 * HeartBeatHandler
 *
 * 1. 初始化 [io.intellij.kt.netty.tcpfrp.protocol.channel.DispatchManager]
 * 2. 处理心跳
 * 
 * @author tech@intellij.io
 */
class HeartBeatHandler : SimpleChannelInboundHandler<Ping>() {

  companion object {
    private val log = getLogger(HeartBeatHandler::class.java)
  }

  /**
   * Triggered from [io.intellij.kt.netty.tcpfrp.server.handlers.initial.ListeningRequestHandler]
   */
  @Throws(Exception::class)
  override fun channelActive(ctx: ChannelHandlerContext) {
    log.info("[channelActive]: Ping Handler")
    ctx.channel().initDispatchManager()
    ctx.channel().getFrpChannel().activeRead()
  }

  @Throws(Exception::class)
  override fun channelRead0(ctx: ChannelHandlerContext, ping: Ping) {
    log.info("HeatBeat PING|{}", ping)
    val frpChannel: FrpChannel = ctx.channel().getFrpChannel()
    frpChannel.write(Pong.build(ping.name))
  }

  @Throws(Exception::class)
  override fun channelReadComplete(ctx: ChannelHandlerContext) {
    ctx.channel().getFrpChannel()
      .flush()
      .activeRead()
  }

  @Throws(Exception::class)
  override fun channelInactive(ctx: ChannelHandlerContext) {
    log.warn("release dispatch channel")
    val frpChannel: FrpChannel = ctx.channel().getFrpChannel()
    frpChannel.getDispatchManager().releaseAll()
    super.channelInactive(ctx)

    log.warn("close frp channel")
    frpChannel.close()
  }
}