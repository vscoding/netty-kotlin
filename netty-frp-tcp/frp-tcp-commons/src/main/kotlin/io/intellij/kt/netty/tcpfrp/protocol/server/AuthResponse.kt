package io.intellij.kt.netty.tcpfrp.protocol.server

import io.intellij.kt.netty.tcpfrp.protocol.FrpBasicMsg
import org.jetbrains.annotations.Contract

/**
 * AuthResponse
 *
 * @author tech@intellij.io
 */
data class AuthResponse(
    val success: Boolean // 是否认证成功
) {
    companion object {
        fun success(): FrpBasicMsg {
            return FrpBasicMsg.createAuthResponse(AuthResponse(true))
        }

        fun failure(): FrpBasicMsg {
            return FrpBasicMsg.createAuthResponse(AuthResponse(false))
        }

    }
}
