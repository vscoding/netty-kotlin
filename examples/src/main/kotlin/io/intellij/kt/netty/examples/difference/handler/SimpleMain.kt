package io.intellij.kt.netty.examples.difference.handler

import io.intellij.kt.netty.commons.getLogger
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.embedded.EmbeddedChannel
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

    @JvmStatic
    fun main(args: Array<String>) {
        val channel = EmbeddedChannel(object : SimpleChannelInboundHandler<ByteBuf>() {

            @Throws(Exception::class)
            override fun channelActive(ctx: ChannelHandlerContext) {
                log.info("channel active")
            }

            @Throws(Exception::class)
            override fun channelRead0(ctx: ChannelHandlerContext, msg: ByteBuf) {
                val i = msg.readableBytes()
                val bytes = ByteArray(i)

                msg.getBytes(msg.readerIndex(), bytes)

                log.info("receive msg: {}", String(bytes))

                log.info("当前的 ByteBuf 引用计数: {}", msg.refCnt())
                ReferenceCountUtil.retain(msg) // SimpleChannelInboundHandler 的 ByteBuf 引用计数器会主动 -1 (默认的)
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

        val content = "Hello, Netty Simple Handler!"
        val buf = Unpooled.copiedBuffer(content.toByteArray())

        channel.writeInbound(buf)
        channel.finish()

        val rtBuf = channel.readOutbound<ByteBuf>()
        rtBuf.readableBytes().also {
            val bytes = ByteArray(it)
            rtBuf.readBytes(bytes)
            log.info("read msg: {}", String(bytes))

            channel.close()
        }
    }

}