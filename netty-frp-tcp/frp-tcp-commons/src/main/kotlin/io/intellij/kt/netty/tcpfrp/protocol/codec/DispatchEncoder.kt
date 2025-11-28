package io.intellij.kt.netty.tcpfrp.protocol.codec

import io.intellij.kt.netty.tcpfrp.protocol.FrpMsgType
import io.intellij.kt.netty.tcpfrp.protocol.channel.DispatchPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import java.nio.charset.StandardCharsets

/**
 * DispatchEncoder
 *
 * @author tech@intellij.io
 */
class DispatchEncoder : MessageToByteEncoder<DispatchPacket>() {

    @Throws(Exception::class)
    override fun encode(ctx: ChannelHandlerContext?, msg: DispatchPacket, out: ByteBuf) {

        // type
        out.writeByte(FrpMsgType.DATA_PACKET.type)

        // dispatchId
        out.writeBytes(msg.dispatchId.toByteArray(StandardCharsets.UTF_8))

        // len
        val length: Int = msg.packet.readableBytes()
        out.writeInt(length)

        // data
        val packet: ByteBuf = msg.packet
        out.writeBytes(packet)
    }
}
