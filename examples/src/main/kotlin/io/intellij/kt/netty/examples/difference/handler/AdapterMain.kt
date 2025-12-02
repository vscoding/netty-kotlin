package io.intellij.kt.netty.examples.difference.handler

import io.intellij.kt.netty.commons.getLogger
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.embedded.EmbeddedChannel

/**
 * AdapterMain
 *
 * ChannelInboundHandlerAdapter 的 ByteBuf 引用计数器不会主动 -1
 *
 * @author tech@intellij.io
 */
object AdapterMain {
    private val log = getLogger("AdapterHandler")

    @JvmStatic
    fun main(args: Array<String>) {

        val channel = EmbeddedChannel(object : ChannelInboundHandlerAdapter() {
            @Throws(Exception::class)
            override fun channelActive(ctx: ChannelHandlerContext) {
                log.info("channel active")
            }

            @Throws(Exception::class)
            override fun channelInactive(ctx: ChannelHandlerContext) {
                log.info("channel inactive")
            }

            @Throws(Exception::class)
            override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
                if (msg is ByteBuf) {
                    val i = msg.readableBytes()
                    val bytes = ByteArray(i)

                    // 使用 getBytes 不推进 readerIndex，避免影响 buf 的写指针布局
                    msg.getBytes(msg.readerIndex(), bytes)

                    log.info("当前的 ByteBuf 引用计数: {}", msg.refCnt())
                    log.info("channel read: {}", String(bytes))

                    // 直接复用 msg
                    ctx.write(msg)
                }
            }

            @Throws(Exception::class)
            override fun channelReadComplete(ctx: ChannelHandlerContext) {
                ctx.flush()
            }

            @Throws(Exception::class)
            override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
                log.error("exception caught: {}", cause.message)
            }
        })


        val content = "Hello, Netty Adapter Handler!"
        val buf = Unpooled.wrappedBuffer(content.toByteArray())

        channel.writeInbound(buf)
        channel.finish()

        val rtBuf = channel.readOutbound<ByteBuf>()
        val len = rtBuf.readableBytes()
        val bytes = ByteArray(len)
        rtBuf.readBytes(bytes)

        log.info("read msg: {}", bytes.toString(Charsets.UTF_8))

        channel.close()
    }

}