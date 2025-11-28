package io.intellij.kt.netty.tcpfrp.protocol.client

import io.intellij.kt.netty.tcpfrp.protocol.FrpBasicMsg

/**
 * AuthRequest
 *
 * @author tech@intellij.io
 */
data class AuthRequest(val token: String) {
    companion object {
        fun create(token: String): FrpBasicMsg {
            return FrpBasicMsg.createAuthRequest(AuthRequest(token))
        }
    }
}