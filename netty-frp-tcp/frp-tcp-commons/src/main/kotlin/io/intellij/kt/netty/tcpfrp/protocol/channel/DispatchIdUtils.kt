package io.intellij.kt.netty.tcpfrp.protocol.channel

import io.netty.channel.Channel

/**
 * DispatchIdUtils
 *
 * @author tech@intellij.io
 */
object DispatchIdUtils {
    const val ID_LENGTH: Int = 60

    fun generateId(channel: Channel): String {
        return channel.id().asLongText()
    }

}