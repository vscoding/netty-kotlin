package io.intellij.kt.netty.server.socks.config

/**
 * Properties
 *
 * @author tech@intellij.io
 */
object Properties {
    val PORT = System.getProperty("port", "1080").toInt()
}