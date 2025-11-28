package io.intellij.kt.netty.tcp.lb.config

/**
 * Backend
 *
 * @author tech@intellij.io
 */
data class Backend(
    val name: String,
    val host: String,
    val port: Int
) {
    override fun toString(): String = "name='$name', host='$host', port=$port"
}
