package io.intellij.kt.netty.tcpfrp.protocol.client

import io.intellij.kt.netty.tcpfrp.protocol.FrpBasicMsg

/**
 * AuthRequest
 *
 * @author tech@intellij.io
 */
data class AuthRequest(val token: String) {
    companion object {
        fun build(token: String): FrpBasicMsg {
            return FrpBasicMsg.buildAuthRequest(AuthRequest(token))
        }
    }
}