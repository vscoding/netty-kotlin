package io.intellij.kt.netty.server.socks.handlers.connect

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.commons.utils.ChannelUtils.closeOnFlush
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelOption
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.socksx.v4.DefaultSocks4CommandResponse
import io.netty.handler.codec.socksx.v4.Socks4CommandRequest
import io.netty.handler.codec.socksx.v4.Socks4CommandStatus
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.FutureListener

/**
 * Socks4ServerConnectHandler
 *
 * @author tech@intellij.io
 */
class Socks4ServerConnectHandler : SimpleChannelInboundHandler<Socks4CommandRequest>() {
    companion object {
        private val log = getLogger(Socks4ServerConnectHandler::class.java)
    }

    private val b = Bootstrap()

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, request: Socks4CommandRequest) {
        val inboundChannel = ctx.channel()
        val promise = ctx.executor().newPromise<Channel>()
        promise.addListener(FutureListener { future: Future<Channel> ->
            val outboundChannel = future.getNow()
            if (future.isSuccess) {
                val responseFuture = ctx.channel().writeAndFlush(
                    DefaultSocks4CommandResponse(Socks4CommandStatus.SUCCESS)
                )
                responseFuture.addListener(ChannelFutureListener { channelFuture: ChannelFuture? ->
                    ctx.pipeline().remove(this@Socks4ServerConnectHandler)
                    outboundChannel.pipeline().addLast(RelayHandler(inboundChannel))
                    ctx.pipeline().addLast(RelayHandler(outboundChannel))
                })
            } else {
                ctx.channel().writeAndFlush(DefaultSocks4CommandResponse(Socks4CommandStatus.REJECTED_OR_FAILED))
                closeOnFlush(ctx.channel())
            }
        })

        val dstAddr = request.dstAddr()
        val dstPort = request.dstPort()
        b.group(inboundChannel.eventLoop())
            .channel(NioSocketChannel::class.java)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .handler(DirectClientHandler(promise))

        b.connect(dstAddr, dstPort).addListener(ChannelFutureListener { future: ChannelFuture ->
            if (future.isSuccess) {
                log.info("connect to {}:{} success", dstAddr, dstPort)
            } else {
                // Close the connection if the connection attempt has failed.
                log.error("connect to {}:{} failed", request.dstAddr(), request.dstPort())
                ctx.channel().writeAndFlush(
                    DefaultSocks4CommandResponse(Socks4CommandStatus.REJECTED_OR_FAILED)
                )
                closeOnFlush(inboundChannel)
            }
        })
    }
}
