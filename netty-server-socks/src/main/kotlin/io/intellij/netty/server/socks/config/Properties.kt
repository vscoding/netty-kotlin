package io.intellij.netty.server.socks.config

/**
 * Properties
 *
 * @author tech@intellij.io
 */
object Properties {
    val PORT = System.getProperty("port", "1080").toInt()
}