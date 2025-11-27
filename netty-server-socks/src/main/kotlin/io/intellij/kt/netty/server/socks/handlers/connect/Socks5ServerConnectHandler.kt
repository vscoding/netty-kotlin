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
import io.netty.handler.codec.socksx.v4.Socks4CommandStatus
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandResponse
import io.netty.handler.codec.socksx.v5.Socks5CommandRequest
import io.netty.handler.codec.socksx.v5.Socks5CommandStatus
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.FutureListener

/**
 * Socks5ServerConnectHandler
 *
 * @author tech@intellij.io
 */
class Socks5ServerConnectHandler : SimpleChannelInboundHandler<Socks5CommandRequest>() {
    companion object {
        private val log = getLogger(Socks5ServerConnectHandler::class.java)
    }

    private val b = Bootstrap()

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, request: Socks5CommandRequest) {
        val inboundChannel = ctx.channel()
        val promise = ctx.executor().newPromise<Channel>()
        promise.addListener(FutureListener { future: Future<Channel> ->
            val outboundChannel = future.getNow()
            if (future.isSuccess) {
                val responseFuture = ctx.channel().writeAndFlush(
                    DefaultSocks5CommandResponse(
                        Socks5CommandStatus.SUCCESS, request.dstAddrType(), request.dstAddr(), request.dstPort()
                    )
                )
                responseFuture.addListener(ChannelFutureListener { channelFuture: ChannelFuture? ->
                    ctx.pipeline().remove(this@Socks5ServerConnectHandler)
                    outboundChannel.pipeline().addLast(RelayHandler(inboundChannel))
                    ctx.pipeline().addLast(RelayHandler(outboundChannel))
                })
            } else {
                ctx.channel()
                    .writeAndFlush(DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, request.dstAddrType()))
                closeOnFlush(inboundChannel)
            }
        })

        b.group(inboundChannel.eventLoop())
            .channel(NioSocketChannel::class.java)
            .option<Int?>(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
            .option<Boolean?>(ChannelOption.SO_KEEPALIVE, true)
            .handler(DirectClientHandler(promise))

        b.connect(request.dstAddr(), request.dstPort()).addListener(ChannelFutureListener { future: ChannelFuture ->
            if (future.isSuccess) {
                log.info("connect to {}:{} success", request.dstAddr(), request.dstPort())
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
