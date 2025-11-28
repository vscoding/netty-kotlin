package io.intellij.kt.netty.tcpfrp.protocol.heartbeat

import io.intellij.kt.netty.tcpfrp.protocol.FrpBasicMsg
import java.util.Date

/**
 * Pong
 *
 * @author tech@intellij.io
 */
data class Pong(
    val time: Date,
    val name: String
) {
    companion object {
        fun create(name: String): FrpBasicMsg {
            return FrpBasicMsg.createPong(Pong(Date(), name))
        }
    }
}