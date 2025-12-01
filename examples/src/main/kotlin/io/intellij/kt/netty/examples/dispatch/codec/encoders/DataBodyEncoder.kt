package io.intellij.kt.netty.examples.dispatch.codec.encoders

import com.alibaba.fastjson2.JSON
import io.intellij.kt.netty.examples.dispatch.model.DataBody
import io.intellij.kt.netty.examples.dispatch.protocol.ProtocolMsgType
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

/**
 * DataBodyEncoder
 *
 * @author tech@intellij.io
 */
@ChannelHandler.Sharable
class DataBodyEncoder : MessageToByteEncoder<DataBody>() {

    @Throws(Exception::class)
    override fun encode(channelHandlerContext: ChannelHandlerContext, dataBody: DataBody, byteBuf: ByteBuf) {
        byteBuf.writeInt(ProtocolMsgType.DATA.type)

        val jsonString = JSON.toJSONString(dataBody)
        val bytes = jsonString.toByteArray()

        byteBuf.writeInt(bytes.size)
        byteBuf.writeBytes(bytes)
    }

}
