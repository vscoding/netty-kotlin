package io.intellij.kt.netty.tcpfrp.protocol.client

import io.intellij.kt.netty.tcpfrp.protocol.FrpBasicMsg

/**
 * ListeningRequest
 *
 * @author tech@intellij.io
 */
data class ListeningRequest(
    val listeningPorts: List<Int>
) {

    companion object {
        fun create(listeningPorts: List<Int>): FrpBasicMsg {
            return FrpBasicMsg.createListeningRequest(ListeningRequest(listeningPorts))
        }
    }

}
