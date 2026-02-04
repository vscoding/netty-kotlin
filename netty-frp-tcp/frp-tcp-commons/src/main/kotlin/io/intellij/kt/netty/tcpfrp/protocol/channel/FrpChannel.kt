package io.intellij.kt.netty.tcpfrp.protocol.channel

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.tcpfrp.protocol.FrpBasicMsg
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener
import io.netty.util.AttributeKey

/**
 * FrpChannel
 *
 * @author tech@intellij.io
 */
data class FrpChannel(
    val ch: Channel
) {

    companion object {
        private val log = getLogger(FrpChannel::class.java)

        val FRP_CHANNEL_KEY: AttributeKey<FrpChannel> = AttributeKey.valueOf<FrpChannel>("frpChannel")

        fun buildIn(ch: Channel) {
            val frpChannel = FrpChannel(ch)
            ch.attr(FRP_CHANNEL_KEY).set(frpChannel)
        }

        fun getBy(ch: Channel): FrpChannel {
            return ch.attr(FRP_CHANNEL_KEY).get()
        }

    }

    fun write(dispatchPacket: DispatchPacket, vararg listeners: ChannelFutureListener): ChannelFuture {
        return this.writeObj(dispatchPacket, *listeners)
    }

    fun write(dispatchPacket: FrpBasicMsg, vararg listeners: ChannelFutureListener): ChannelFuture {
        return this.writeObj(dispatchPacket, *listeners)
    }

    fun flush(): FrpChannel {
        if (ch.isActive) {
            ch.flush()
        }
        return this
    }

    fun writeAndFlushEmpty(vararg listeners: ChannelFutureListener): ChannelFuture {
        return this.writeAndFlushObj(Unpooled.EMPTY_BUFFER, *listeners)
    }

    fun writeAndFlush(dispatchPacket: DispatchPacket, vararg listeners: ChannelFutureListener): ChannelFuture {
        return this.writeAndFlushObj(dispatchPacket, *listeners)
    }

    fun writeAndFlush(basicMsg: FrpBasicMsg, vararg listeners: ChannelFutureListener): ChannelFuture {
        return this.writeAndFlushObj(basicMsg, *listeners)
    }


    @Throws(Exception::class)
    private fun writeObj(msg: Any, vararg listeners: ChannelFutureListener): ChannelFuture {
        if (ch.isActive) {
            return ch.writeAndFlush(msg).addListeners(*listeners)
        }
        log.error("Channel is not active(or is null), cannot write message")
        throw RuntimeException("Channel is not active(or is null), cannot write message")
    }

    @Throws(Exception::class)
    private fun writeAndFlushObj(msg: Any, vararg listeners: ChannelFutureListener): ChannelFuture {
        if (ch.isActive) {
            return ch.writeAndFlush(msg).addListeners(*listeners)
        }
        log.error("Channel is not active(or is null), cannot write and flush message")
        throw RuntimeException("Channel is not active(or is null), cannot write and flush message")
    }


    fun read() {
        if (ch.isActive) {
            ch.read()
        } else {
            log.error("Channel is not active(or is null), cannot read message")
        }
    }

    fun close() {
        if (ch.isActive) {
            ch.close()
        }
    }
}
