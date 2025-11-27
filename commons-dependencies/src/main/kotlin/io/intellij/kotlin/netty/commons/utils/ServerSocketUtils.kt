package io.intellij.kotlin.netty.commons.utils

import java.io.IOException
import java.net.InetSocketAddress
import java.net.ServerSocket

/**
 * ServerSocketUtils
 *
 * @author tech@intellij.io
 */
object ServerSocketUtils {

    /**
     * Checks if a specific port is currently in use on the local machine.
     *
     * @param port the port number to check
     * @return true if the port is in use, false otherwise
     */
    fun isPortInUse(port: Int): Boolean {
        try {
            ServerSocket().use { serverSocket ->
                serverSocket.setReuseAddress(true)
                serverSocket.bind(InetSocketAddress(port))
                return false // 端口未被占用
            }
        } catch (e: IOException) {
            return true // 端口被占用
        }
    }
}
