package io.intellij.kt.netty.spring.boot.entities

/**
 * ServerStartResult
 *
 * @author tech@intellij.io
 */
data class ServerRunRes(
    val status: Boolean,
    val msg: String = ""
)
