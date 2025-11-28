package io.intellij.kt.netty.tcpfrp.protocol.codec

import com.alibaba.fastjson2.JSON
import io.intellij.kt.netty.tcpfrp.protocol.FrpBasicMsg
import io.intellij.kt.netty.tcpfrp.protocol.FrpMsgType
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

/**
 * FrpBasicMsgEncoder
 *
 * @author tech@intellij.io
 */
class FrpBasicMsgEncoder : MessageToByteEncoder<FrpBasicMsg>() {

    @Throws(Exception::class)
    override fun encode(ctx: ChannelHandlerContext, basicMsg: FrpBasicMsg, out: ByteBuf) {
        val msgType: FrpMsgType = basicMsg.msgType

        out.writeByte(msgType.type)

        val msgBody: Any = basicMsg.msgBody

        val json: String = JSON.toJSONString(msgBody)
        val bytes = json.toByteArray()

        out.writeInt(bytes.size)
        out.writeBytes(bytes)
    }

}
