package io.intellij.kt.netty.examples.dispatch.codec

import com.alibaba.fastjson2.JSON
import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.examples.dispatch.model.DataBody
import io.intellij.kt.netty.examples.dispatch.model.HeartBeat
import io.intellij.kt.netty.examples.dispatch.protocol.ProtocolMsgType
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

/**
 * DispatchDecoder
 *
 * @author tech@intellij.io
 */
class DispatchDecoder : ByteToMessageDecoder() {

    companion object {
        private val log = getLogger(DispatchDecoder::class.java)
    }

    @Throws(Exception::class)
    override fun decode(ctx: ChannelHandlerContext?, inBuf: ByteBuf, out: MutableList<Any>) {
        inBuf.markReaderIndex()
        if (inBuf.readableBytes() < 4) {
            inBuf.resetReaderIndex()
            return
        }
        val type = inBuf.readInt()
        val msgType = ProtocolMsgType.get(type) ?: throw RuntimeException("Illegal protocol type")

        if (inBuf.readableBytes() < 4) {
            inBuf.resetReaderIndex()
            return
        }

        val len = inBuf.readInt()
        if (inBuf.readableBytes() < len) {
            inBuf.resetReaderIndex()
            return
        }
        val bytes = ByteArray(len)
        inBuf.readBytes(bytes)
        val msgJson = String(bytes)

        if (ProtocolMsgType.HEARTBEAT === msgType) {
            out.add(JSON.parseObject(msgJson, HeartBeat::class.java))
        }

        if (ProtocolMsgType.DATA === msgType) {
            out.add(JSON.parseObject(msgJson, DataBody::class.java))
        }
    }

    @Throws(Exception::class)
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable?) {
        log.error("DispatchDecoder error", cause)
        ctx.close()
    }

}
