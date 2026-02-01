package io.intellij.kt.netty.tcpfrp.protocol

import io.intellij.kt.netty.tcpfrp.protocol.client.AuthRequest
import io.intellij.kt.netty.tcpfrp.protocol.client.ListeningRequest
import io.intellij.kt.netty.tcpfrp.protocol.client.ServiceState
import io.intellij.kt.netty.tcpfrp.protocol.heartbeat.Ping
import io.intellij.kt.netty.tcpfrp.protocol.heartbeat.Pong
import io.intellij.kt.netty.tcpfrp.protocol.server.AuthResponse
import io.intellij.kt.netty.tcpfrp.protocol.server.ListeningResponse
import io.intellij.kt.netty.tcpfrp.protocol.server.UserState

/**
 * FrpBasicMsg
 *
 * @author tech@intellij.io
 */
data class FrpBasicMsg(
    val msgType: FrpMsgType,
    val msgBody: Any
) {

    enum class State {
        READ_TYPE,
        READ_LENGTH,
        READ_BASIC_MSG,
        READ_DISPATCH_PACKET
    }

    companion object {
        fun build(msgType: FrpMsgType, msgBody: Any): FrpBasicMsg {
            return FrpBasicMsg(msgType, msgBody)
        }

        fun buildAuthRequest(authRequest: AuthRequest): FrpBasicMsg =
            build(FrpMsgType.AUTH_REQUEST, authRequest)

        fun buildAuthResponse(authResponse: AuthResponse): FrpBasicMsg =
            build(FrpMsgType.AUTH_RESPONSE, authResponse)

        fun buildListeningRequest(listeningRequest: ListeningRequest): FrpBasicMsg =
            build(FrpMsgType.LISTENING_REQUEST, listeningRequest)

        fun buildListeningResponse(listeningResponse: ListeningResponse): FrpBasicMsg =
            build(FrpMsgType.LISTENING_RESPONSE, listeningResponse)

        fun buildUserState(userState: UserState): FrpBasicMsg =
            build(FrpMsgType.USER_STATE, userState)

        fun buildServiceState(serviceState: ServiceState): FrpBasicMsg =
            build(FrpMsgType.SERVICE_STATE, serviceState)

        fun buildPing(ping: Ping): FrpBasicMsg =
            build(FrpMsgType.PING, ping)

        fun buildPong(pong: Pong): FrpBasicMsg =
            build(FrpMsgType.PONG, pong)

    }

}
