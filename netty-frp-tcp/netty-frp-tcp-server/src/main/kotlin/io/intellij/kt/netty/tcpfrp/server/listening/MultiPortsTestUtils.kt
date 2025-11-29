package io.intellij.kt.netty.tcpfrp.server.listening

import io.intellij.kt.netty.commons.utils.ServerSocketUtils
import io.intellij.kt.netty.tcpfrp.protocol.server.ListeningResponse

/**
 * MultiPortUtils
 *
 * @author tech@intellij.io
 */
object MultiPortsTestUtils {

    fun test(listeningPorts: List<Int>): ListeningResponse {
        val listeningStatus = HashMap<Int, Boolean>()
        for (port in listeningPorts) {
            val portInUse: Boolean = ServerSocketUtils.isPortInUse(port)
            listeningStatus[port] = portInUse
        }
        return ListeningResponse(
            success = listeningStatus.values.stream().noneMatch { b: Boolean? -> b!! },
            listeningStatus = listeningStatus
        )
    }

}