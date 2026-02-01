package io.intellij.kt.netty.tcpfrp.protocol.client

import io.intellij.kt.netty.tcpfrp.protocol.ConnState
import io.intellij.kt.netty.tcpfrp.protocol.FrpBasicMsg

/**
 * ServiceState
 *
 * @author tech@intellij.io
 */
data class ServiceState(
    val stateName: String,
    val dispatchId: String
) {

    companion object {
        fun success(dispatchId: String): FrpBasicMsg {
            return FrpBasicMsg.buildServiceState(
                ServiceState(ConnState.SUCCESS.stateName, dispatchId)
            )
        }

        fun failure(dispatchId: String): FrpBasicMsg {
            return FrpBasicMsg.buildServiceState(
                ServiceState(ConnState.FAILURE.stateName, dispatchId)
            )
        }

        fun broken(dispatchId: String): FrpBasicMsg {
            return FrpBasicMsg.buildServiceState(
                ServiceState(ConnState.BROKEN.stateName, dispatchId)
            )
        }
    }

}
