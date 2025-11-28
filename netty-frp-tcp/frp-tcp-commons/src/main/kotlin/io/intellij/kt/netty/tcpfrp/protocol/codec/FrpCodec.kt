package io.intellij.kt.netty.tcpfrp.protocol.codec

import io.netty.channel.ChannelInboundHandler
import io.netty.channel.ChannelOutboundHandler

/**
 * FrpCodec
 *
 * @author tech@intellij.io
 */
object FrpCodec {

    /**
     * Creates a new instance of [ChannelInboundHandler] configured for client-side decoding of FRP messages.
     * The decoder processes incoming messages in client mode, interpreting specific message types and formats.
     *
     * @return a [ChannelInboundHandler] instance for decoding messages in client mode
     */
    fun clientDecoder(): ChannelInboundHandler {
        return FrpDecoder(FrpDecoder.Mode.CLIENT)
    }

    /**
     * Creates a new instance of [ChannelInboundHandler] configured for server-side decoding of FRP messages.
     * The decoder processes incoming messages in server mode, interpreting specific message types and formats.
     *
     * @return a [ChannelInboundHandler] instance for decoding messages in server mode
     */
    fun serverDecoder(): ChannelInboundHandler {
        return FrpDecoder(FrpDecoder.Mode.SERVER)
    }

    /**
     * Creates a new instance of [ChannelOutboundHandler] for encoding basic FRP messages.
     * The encoder is responsible for serializing basic message types into the specified protocol format
     * before transmission over the network.
     *
     * @return a [ChannelOutboundHandler] instance for encoding basic FRP messages
     */
    fun basicMsgEncoder(): ChannelOutboundHandler {
        return FrpBasicMsgEncoder()
    }

    /**
     * Creates a new instance of [ChannelOutboundHandler] for encoding DispatchPackets.
     * The encoder serializes a [io.intellij.kt.netty.tcpfrp.protocol.channel.DispatchPacket] into the appropriate byte format, which consists of:
     * type, dispatch ID, length, and the byte buffer of the dispatch packet.
     *
     * @return a [ChannelOutboundHandler] instance for encoding DispatchPackets
     */
    fun dispatchEncoder(): ChannelOutboundHandler {
        return DispatchEncoder()
    }

}