package io.intellij.kt.netty.tcpfrp.protocol.channel

import io.intellij.kt.netty.tcpfrp.protocol.codec.DispatchEncoder
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.embedded.EmbeddedChannel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DispatchPacketTest {

  @Test
  fun `dispatch encoder releases holder and payload after outbound write`() {
    val channel = EmbeddedChannel(DispatchEncoder())
    val payload = Unpooled.buffer().writeBytes(byteArrayOf(1, 2, 3))
    val packet = DispatchPacket.create(dispatchId(), payload)

    assertTrue(channel.writeOutbound(packet))

    assertEquals(0, packet.refCnt())
    assertEquals(0, payload.refCnt())

    val encoded = channel.readOutbound<ByteBuf>()
    assertEquals(1 + DispatchIdUtils.ID_LENGTH + 4 + 3, encoded.readableBytes())
    encoded.release()

    assertFalse(channel.finish())
  }

  @Test
  fun `dispatch forwarding retains payload for target channel only`() {
    val target = EmbeddedChannel()
    val manager = DispatchManager(mutableMapOf(dispatchId() to target))
    val inbound = EmbeddedChannel(
      object : SimpleChannelInboundHandler<DispatchPacket>() {
        override fun channelRead0(ctx: ChannelHandlerContext, msg: DispatchPacket) {
          manager.dispatch(msg)
        }
      },
    )
    val payload = Unpooled.buffer().writeBytes(byteArrayOf(7, 8))
    val packet = DispatchPacket.create(dispatchId(), payload)

    inbound.writeInbound(packet)

    assertEquals(0, packet.refCnt())
    assertEquals(1, payload.refCnt())

    val forwarded = target.readOutbound<ByteBuf>()
    assertEquals(2, forwarded.readableBytes())
    forwarded.release()

    assertEquals(0, payload.refCnt())
    assertFalse(inbound.finish())
    assertFalse(target.finish())
  }

  @Test
  fun `dispatch drop path releases packet through inbound auto release`() {
    val manager = DispatchManager()
    val inbound = EmbeddedChannel(
      object : SimpleChannelInboundHandler<DispatchPacket>() {
        override fun channelRead0(ctx: ChannelHandlerContext, msg: DispatchPacket) {
          manager.dispatch(msg)
        }
      },
    )
    val payload = Unpooled.buffer().writeBytes(byteArrayOf(9))
    val packet = DispatchPacket.create(dispatchId(), payload)

    inbound.writeInbound(packet)

    assertEquals(0, packet.refCnt())
    assertEquals(0, payload.refCnt())
    assertFalse(inbound.finish())
  }

  private fun dispatchId(): String {
    return "a".repeat(DispatchIdUtils.ID_LENGTH)
  }
}
