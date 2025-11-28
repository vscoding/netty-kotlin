package io.intellij.kt.netty.tcpfrp.protocol.channel

import io.netty.buffer.ByteBuf

/**
 * DispatchPacket
 *
 * @author tech@intellij.io
 */
data class DispatchPacket(
    val dispatchId: String,
    val packet: ByteBuf
) {
    companion object {
        fun create(dispatchId: String, packet: ByteBuf): DispatchPacket {
            return DispatchPacket(dispatchId, packet)
        }

        fun createAndRetain(dispatchId: String, packet: ByteBuf): DispatchPacket {
            return DispatchPacket(dispatchId, packet.retain())
        }
    }
}
