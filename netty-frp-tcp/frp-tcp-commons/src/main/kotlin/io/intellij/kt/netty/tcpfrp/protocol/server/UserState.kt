package io.intellij.kt.netty.tcpfrp.protocol.server

import io.intellij.kt.netty.tcpfrp.protocol.ConnState
import io.intellij.kt.netty.tcpfrp.protocol.FrpBasicMsg

/**
 * UserState
 *
 * @author tech@intellij.io
 */
data class UserState(
    val stateName: String,
    val dispatchId: String,
    val listeningPort: Int? = null
) {

    companion object {
        fun accept(dispatchId: String, listeningPort: Int): FrpBasicMsg {
            return FrpBasicMsg.buildUserState(
                UserState(ConnState.ACCEPT.stateName, dispatchId, listeningPort)
            )
        }

        fun ready(dispatchId: String): FrpBasicMsg {
            return FrpBasicMsg.buildUserState(
                UserState(ConnState.READY.stateName, dispatchId)
            )
        }

        fun broken(dispatchId: String): FrpBasicMsg {
            return FrpBasicMsg.buildUserState(
                UserState(ConnState.BROKEN.stateName, dispatchId)
            )
        }
    }

}