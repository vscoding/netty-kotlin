package io.intellij.kt.netty.server.tcpproxy.handler

import io.netty.bootstrap.Bootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelOption

/**
 * HexDumpProxyFrontendHandler
 *
 * @author tech@intellij.io
 */
class HexDumpProxyFrontendHandler(
    val remoteHost: String,
    val remotePort: Int
) : ChannelInboundHandlerAdapter() {

    // As we use inboundChannel.eventLoop() when building the Bootstrap, this does not need to be volatile as
    // the outboundChannel will use the same EventLoop (and therefore Thread) as the inboundChannel.
    // 这个channel是连接远程服务器的channel
    private var outboundChannel: Channel? = null

    @Throws(Exception::class)
    override fun channelActive(ctx: ChannelHandlerContext) {
        val inboundChannel = ctx.channel()

        // Start the connection attempt.
        val b = Bootstrap()
        b.group(inboundChannel.eventLoop())
            .channel(ctx.channel().javaClass) // 切入点1
            .handler(HexDumpProxyBackendHandler(inboundChannel))
            .option(ChannelOption.AUTO_READ, false)
        val f = b.connect(remoteHost, remotePort)
        outboundChannel = f.channel()
        f.addListener(object : ChannelFutureListener {
            @Throws(Exception::class)
            override fun operationComplete(future: ChannelFuture) {
                if (future.isSuccess) {
                    // connection complete start to read first data
                    inboundChannel.read()
                } else {
                    // Close the connection if the connection attempt has failed.
                    inboundChannel.close()
                }
            }
        })
    }

    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        outboundChannel?.also {
            if (it.isActive) {
                it.writeAndFlush(msg).addListener(
                    ChannelFutureListener { future: ChannelFuture ->
                        if (future.isSuccess) {
                            // was able to flush out data, start to read the next chunk
                            // 切入点
                            ctx.channel().read()
                        } else {
                            future.channel().close()
                        }
                    }
                )
            }
        }
    }

    @Throws(Exception::class)
    override fun channelInactive(ctx: ChannelHandlerContext) {
        outboundChannel?.also {
            closeOnFlush(it)
        }
    }

    @Throws(Exception::class)
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable?) {
        outboundChannel?.also {
            closeOnFlush(it)
        }
    }

    companion object {
        /**
         * Close on the flush.
         *
         * @param ch the ch
         */
        fun closeOnFlush(ch: Channel) {
            if (ch.isActive) {
                ch.writeAndFlush(Unpooled.EMPTY_BUFFER)
                    .addListener(ChannelFutureListener.CLOSE)
            }
        }
    }
}
