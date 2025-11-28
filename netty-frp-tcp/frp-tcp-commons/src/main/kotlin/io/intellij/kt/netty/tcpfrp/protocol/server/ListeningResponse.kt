package io.intellij.kt.netty.tcpfrp.protocol.server

/**
 * ListeningResponse
 *
 * @author tech@intellij.io
 */
data class ListeningResponse(
    val success: Boolean, // 是否连接成功
    val listeningStatus: Map<Int, Boolean>, // 监听状态
    val reason: String = "" // 失败原因
)
