package io.intellij.kt.netty.tcpfrp.commons

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.tcpfrp.protocol.channel.DispatchManager
import io.intellij.kt.netty.tcpfrp.protocol.channel.FrpChannel
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener

/**
 * Listeners
 *
 * @author tech@intellij.io
 */
object Listeners {

    private val log = getLogger(Listeners::class.java)

    fun read(): ChannelFutureListener {
        return ChannelFutureListener { f: ChannelFuture ->
            if (f.isSuccess) {
                f.channel().read()
            } else {
                log.error("read failure: ", f.cause())
            }
        }
    }

    fun read(ch: Channel): ChannelFutureListener {
        return read(ch, "")
    }

    fun read(ch: Channel, failureMessage: String): ChannelFutureListener {
        return ChannelFutureListener { f: ChannelFuture ->
            if (f.isSuccess) {
                if (ch.isActive) {
                    ch.read()
                } else {
                    log.error("channel is null or not active, cannot read")
                }
            } else {
                log.error("read failure: {}", failureMessage, f.cause())
            }
        }
    }

    fun read(frpChannel: FrpChannel): ChannelFutureListener {
        return read(frpChannel, "")
    }

    fun read(frpChannel: FrpChannel, failureMessage: String?): ChannelFutureListener {
        return ChannelFutureListener { f: ChannelFuture ->
            if (f.isSuccess) {
                frpChannel.read()
            } else {
                log.error("read failure: {}", failureMessage, f.cause())
            }
        }
    }

    fun releaseDispatchChannel(dispatchManager: DispatchManager, dispatchId: String): ChannelFutureListener {
        return releaseDispatchChannel(dispatchManager, dispatchId, "")
    }

    fun releaseDispatchChannel(
        dispatchManager: DispatchManager,
        dispatchId: String,
        failureMessage: String
    ): ChannelFutureListener {
        return ChannelFutureListener { f: ChannelFuture ->
            if (f.isSuccess) {
                dispatchManager.release(dispatchId, failureMessage)
            } else {
                log.error("release failure: {}", failureMessage, f.cause())
            }
        }
    }

}