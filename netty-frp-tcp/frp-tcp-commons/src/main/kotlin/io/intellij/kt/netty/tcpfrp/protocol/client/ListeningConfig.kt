package io.intellij.kt.netty.tcpfrp.protocol.client

/**
 * ListeningConfig
 *
 * @author tech@intellij.io
 */
data class ListeningConfig(
    val name: String,
    val localIp: String,
    val localPort: Int,
    val remotePort: Int
)