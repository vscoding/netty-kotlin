package io.intellij.kt.netty.examples.dispatch.codec.encoders

import com.alibaba.fastjson2.JSON
import io.intellij.kt.netty.examples.dispatch.model.HeartBeat
import io.intellij.kt.netty.examples.dispatch.protocol.ProtocolMsgType
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

/**
 * HeartBeatEncoder
 *
 * @author tech@intellij.io
 */
@ChannelHandler.Sharable
class HeartBeatEncoder : MessageToByteEncoder<HeartBeat>() {

    @Throws(Exception::class)
    override fun encode(channelHandlerContext: ChannelHandlerContext, heartBeat: HeartBeat, byteBuf: ByteBuf) {
        byteBuf.writeInt(ProtocolMsgType.HEARTBEAT.type)

        val jsonString = JSON.toJSONString(heartBeat)
        val bytes = jsonString.toByteArray()

        byteBuf.writeInt(bytes.size)
        byteBuf.writeBytes(bytes)
    }

}
