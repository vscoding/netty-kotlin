package io.intellij.netty.server.socks.handlers.socks5auth

import io.intellij.kotlin.netty.commons.getLogger
import io.intellij.netty.server.socks.config.Environment.SOCKS5_PASSWORD
import io.intellij.netty.server.socks.config.Environment.SOCKS5_USERNAME
import org.apache.commons.lang3.StringUtils

/**
 * PasswordAuthenticator
 *
 * @author tech@intellij.io
 */
class PasswordAuthenticator : Authenticator {
    private val log = getLogger(PasswordAuthenticator::class.java)
    private var isAuthConfigured: Boolean = false

    init {
        log.info("Retrieving Socks5 username from environment variable 'SOCKS5_USERNAME'")
        log.info("Retrieving Socks5 password from environment variable 'SOCKS5_PASSWORD'")
        this.isAuthConfigured = !SOCKS5_USERNAME.isEmpty() && !SOCKS5_PASSWORD.isEmpty()
        log.info("Socks5 password authentication configured: {}", isAuthConfigured)
    }

    override fun isAuthConfigured(): Boolean {
        return this.isAuthConfigured
    }

    override fun authenticate(username: String, password: String): AuthenticateResponse {
        if (this.isAuthConfigured) {
            if (StringUtils.isBlank(username)) {
                return AuthenticateResponse(false, "username is required")
            }
            if (StringUtils.isBlank(password)) {
                return AuthenticateResponse(false, "password is required")
            }
            return if (SOCKS5_USERNAME == username && SOCKS5_PASSWORD == password) {
                AuthenticateResponse(true, "Authentication successful")
            } else {
                AuthenticateResponse(false, "Invalid username or password")
            }
        } else {
            return AuthenticateResponse(true, "No authentication configured")
        }
    }
}
