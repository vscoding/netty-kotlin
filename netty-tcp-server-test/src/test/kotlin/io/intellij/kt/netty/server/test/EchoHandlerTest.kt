package io.intellij.kt.netty.server.test

import io.intellij.kt.netty.commons.handlers.EchoHandler
import io.intellij.kt.netty.commons.utils.BytesUtils
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.embedded.EmbeddedChannel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * EchoHandlerTest
 *
 * @author tech@intellij.io
 */
class EchoHandlerTest {

    @Throws(Exception::class)
    @Test
    fun `test echo handler`() {
        val msg = "hello world"

        // 1. create EmbeddedChannel
        val channel = EmbeddedChannel(EchoHandler(BytesUtils::printBytes))

        val input = msg.toByteArray()
        val inBuf = Unpooled.wrappedBuffer(input)

        // 2. writeInbound and flush
        channel.writeInbound(inBuf)
        channel.flush()

        // 3. readOutbound
        val outBuf = channel.readOutbound<ByteBuf>()
        val outBytes = ByteArray(outBuf.readableBytes()).also {
            outBuf.readBytes(it)
        }

        Assertions.assertEquals(msg, String(outBytes))
        channel.finish()
    }

}