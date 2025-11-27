package io.intellij.kt.netty.server.socks.config

/**
 * Environment
 *
 * @author tech@intellij.io
 */
object Environment {
    val SOCKS5_USERNAME: String = System.getenv("SOCKS5_USERNAME") ?: ""
    val SOCKS5_PASSWORD: String = System.getenv("SOCKS5_PASSWORD") ?: ""
}
