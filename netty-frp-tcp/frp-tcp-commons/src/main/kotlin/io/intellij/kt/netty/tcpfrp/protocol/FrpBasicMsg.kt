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
        fun create(msgType: FrpMsgType, msgBody: Any): FrpBasicMsg {
            return FrpBasicMsg(msgType, msgBody)
        }

        /**
         * Creates an authentication request message with the provided authentication request data.
         *
         * @param authRequest the authentication request containing necessary authentication details
         * @return an instance of `FrpBasicMsg` representing the authentication request message
         */
        fun createAuthRequest(authRequest: AuthRequest): FrpBasicMsg {
            return create(FrpMsgType.AUTH_REQUEST, authRequest)
        }

        /**
         * Creates an authentication response message with the provided authentication response data.
         *
         * @param authResponse the authentication response containing the result of the authentication attempt
         * @return an instance of `FrpBasicMsg` representing the authentication response message
         */
        fun createAuthResponse(authResponse: AuthResponse): FrpBasicMsg {
            return create(FrpMsgType.AUTH_RESPONSE, authResponse)
        }

        /**
         * Creates a listening request message with the provided `ListeningRequest` data.
         *
         * @param listeningRequest the listening request containing the necessary data for configuring listening ports
         * @return an instance of `FrpBasicMsg` representing the listening request message
         */
        fun createListeningRequest(listeningRequest: ListeningRequest): FrpBasicMsg {
            return create(FrpMsgType.LISTENING_REQUEST, listeningRequest)
        }

        /**
         * Creates a listening response message with the provided `ListeningResponse` data.
         *
         * @param listeningResponse the listening response containing details about the success status,
         * failure reason, or listening status for various configurations
         * @return an instance of `FrpBasicMsg` representing the listening response message
         */
        fun createListeningResponse(listeningResponse: ListeningResponse): FrpBasicMsg {
            return create(FrpMsgType.LISTENING_RESPONSE, listeningResponse)
        }

        /**
         * Creates a user connection state message with the provided `UserState` data.
         *
         * @param userState the user state containing details about the user's connection status, such as state name,
         * dispatch ID, and listening port
         * @return an instance of `FrpBasicMsg` representing the user connection state message
         */
        fun createUserState(userState: UserState): FrpBasicMsg {
            return create(FrpMsgType.USER_STATE, userState)
        }

        /**
         * Creates a service connection state message with the provided `ServiceState` data.
         *
         * @param serviceState the service state containing details such as connection state name and dispatch ID
         * @return an instance of `FrpBasicMsg` representing the service connection state message
         */
        fun createServiceState(serviceState: ServiceState): FrpBasicMsg {
            return create(FrpMsgType.SERVICE_STATE, serviceState)
        }


        /**
         * Creates a ping message with the provided `Ping` data.
         *
         * @param ping the ping object containing the details of the ping message
         * @return an instance of `FrpBasicMsg` representing the ping message
         */
        fun createPing(ping: Ping): FrpBasicMsg {
            return create(FrpMsgType.PING, ping)
        }

        /**
         * Creates a Pong message with the provided `Pong` data.
         *
         * @param pong the Pong object containing the details of the pong message
         * @return an instance of `FrpBasicMsg` representing the pong message
         */
        fun createPong(pong: Pong): FrpBasicMsg {
            return create(FrpMsgType.PONG, pong)
        }

    }

}
