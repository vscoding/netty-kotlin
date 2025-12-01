package io.intellij.kt.netty.examples.replaying

import io.intellij.kt.netty.commons.getLogger
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import tools.jackson.databind.ObjectMapper

/**
 * TestMsgDecoder
 *
 * @author tech@intellij.io
 */
class TestMsgDecoder : ByteToMessageDecoder() {
    companion object {
        private val log = getLogger(TestMsgDecoder::class.java)
        private val MAPPER = ObjectMapper()
    }

    @Throws(Exception::class)
    override fun decode(ctx: ChannelHandlerContext?, `in`: ByteBuf, out: MutableList<Any?>) {
        val i = `in`.readableBytes()
        val bytes = ByteArray(i)
        `in`.readBytes(bytes)
        val json = String(bytes)

        try {
            MAPPER.readTree(json)
            out.add(TestMsg(true, json))
        } catch (e: Exception) {
            log.error("decode error: {}", e.message)
            out.add(TestMsg(false, json))
        }
    }
}