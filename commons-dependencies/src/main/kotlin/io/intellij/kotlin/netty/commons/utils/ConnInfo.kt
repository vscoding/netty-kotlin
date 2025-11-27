package io.intellij.kotlin.netty.commons.utils

/**
 * ConnInfo
 *
 * @author tech@intellij.io
 */
data class ConnInfo(
    val host: String,
    val port: Int
) {
    companion object {
        fun of(host: String, port: Int) = ConnInfo(host, port)
        fun unknown() = ConnInfo("unknown", -1)
    }
}