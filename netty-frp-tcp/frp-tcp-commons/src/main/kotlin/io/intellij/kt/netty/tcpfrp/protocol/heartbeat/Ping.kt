package io.intellij.kt.netty.tcpfrp.protocol.heartbeat

import io.intellij.kt.netty.tcpfrp.protocol.FrpBasicMsg
import java.util.Date

/**
 * Ping
 *
 * @author tech@intellij.io
 */
data class Ping(
    val time: Date,
    val name: String
) {
    companion object {
        fun create(name: String): FrpBasicMsg {
            return FrpBasicMsg.createPing(Ping(Date(), name))
        }
    }
}
