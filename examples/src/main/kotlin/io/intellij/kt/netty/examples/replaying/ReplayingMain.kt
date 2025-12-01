package io.intellij.kt.netty.examples.replaying

import io.intellij.kt.netty.commons.getLogger
import io.netty.buffer.Unpooled
import io.netty.channel.embedded.EmbeddedChannel
import tools.jackson.databind.ObjectMapper

/**
 * ReplayingMain
 *
 * @author tech@intellij.io
 */
object ReplayingMain {
    private val log = getLogger(ReplayingMain::class.java)

    @JvmStatic
    fun main(args: Array<String>) {

        // 创建一个嵌入式通道并设置两个解码器，用于处理入站消息
        val channel = EmbeddedChannel(ContentDecoder(), TestMsgDecoder())

        // 构造数据并将其写入 ByteBuf，用于模拟网络传输的数据包
        val mapper = ObjectMapper()
        val json: String = mapper.writeValueAsString(
            mapOf("name" to "Jack", "age" to 20)
        )

        val bytes = json.toByteArray()
        val msgLength = bytes.size

        val buf = Unpooled.buffer()
        buf.writeInt(msgLength) // Writes the length of the message as an integer.
        buf.writeBytes(bytes) //   Writes the specified byte array into this buffer.

        // 将构造的数据写入通道
        channel.writeInbound(buf)

        // 从通道中读取解码后的消息并打印
        val testMsg = channel.readInbound<TestMsg>()

        // 打印解码后的消息
        log.info("read msg: {}", testMsg)

        // 关闭通道以释放资源
        channel.close()
    }

}