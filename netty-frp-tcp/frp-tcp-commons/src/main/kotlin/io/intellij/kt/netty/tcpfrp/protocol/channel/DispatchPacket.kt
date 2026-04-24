package io.intellij.kt.netty.tcpfrp.protocol.channel

import io.netty.buffer.ByteBuf
import io.netty.buffer.DefaultByteBufHolder

/**
 * DispatchPacket
 *
 * Wraps the payload ByteBuf with dispatch metadata so the packet can move through
 * Netty's pipeline as a single message while still participating in reference counting.
 *
 * @author tech@intellij.io
 */
class DispatchPacket private constructor(
  val dispatchId: String,
  packet: ByteBuf,
) : DefaultByteBufHolder(packet) {
  // Keep the old access shape for existing call sites while delegating ownership to content().
  val packet: ByteBuf
    get() = content()

  companion object {
    // Use when the caller is transferring the current ByteBuf ownership into DispatchPacket.
    fun create(dispatchId: String, packet: ByteBuf): DispatchPacket {
      return DispatchPacket(dispatchId, packet)
    }

    // Use when the payload must stay valid after packet creation, for example decoder slices.
    fun createAndRetain(dispatchId: String, packet: ByteBuf): DispatchPacket {
      return DispatchPacket(dispatchId, packet.retain())
    }
  }

  override fun copy(): DispatchPacket {
    return replace(content().copy())
  }

  override fun duplicate(): DispatchPacket {
    return replace(content().duplicate())
  }

  override fun retainedDuplicate(): DispatchPacket {
    return replace(content().retainedDuplicate())
  }

  override fun replace(content: ByteBuf): DispatchPacket {
    return DispatchPacket(dispatchId, content)
  }

  override fun retain(): DispatchPacket {
    super.retain()
    return this
  }

  override fun retain(increment: Int): DispatchPacket {
    super.retain(increment)
    return this
  }

  override fun touch(): DispatchPacket {
    super.touch()
    return this
  }

  override fun touch(hint: Any?): DispatchPacket {
    super.touch(hint)
    return this
  }

  override fun toString(): String {
    return "DispatchPacket(dispatchId='$dispatchId', readableBytes=${content().readableBytes()}, refCnt=${refCnt()})"
  }
}
