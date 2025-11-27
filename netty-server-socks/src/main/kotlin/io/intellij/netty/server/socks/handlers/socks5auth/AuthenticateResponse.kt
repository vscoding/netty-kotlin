package io.intellij.netty.server.socks.handlers.socks5auth

/**
 * AuthenticateResponse
 *
 * @author tech@intellij.io
 */
data class AuthenticateResponse(
    val success: Boolean,
    val message: String = ""
)
