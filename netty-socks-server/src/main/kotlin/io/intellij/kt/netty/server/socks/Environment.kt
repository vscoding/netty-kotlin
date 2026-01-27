package io.intellij.kt.netty.server.socks

import io.intellij.kt.netty.commons.getLogger

/**
 * Environment manages the server and authentication configuration for the application.
 * It loads settings from system environment variables and provides access to these values.
 *
 * The following configurations are used:
 * - `SERVER_PORT`: Defines the port on which the server will listen to. If not set or invalid, a default value of `1080` is used.
 * - `SOCKS5_USERNAME`: Represents the username for SOCKS5 authentication, retrieved from the environment variable `SOCKS5_USERNAME`.
 * - `SOCKS5_PASSWORD`: Represents the password for SOCKS5 authentication, retrieved from the environment variable `SOCKS5_PASSWORD`.
 *
 * Logging is performed to indicate the state and any issues with configuration loading, such as missing or invalid values.
 *
 * @author tech@intellij.io
 */
object Environment {
    private val log = getLogger(Environment::class.java)

    val PORT: Int get() = _PORT

    val SOCKS5_USERNAME: String = System.getenv("SOCKS5_USERNAME") ?: ""
    val SOCKS5_PASSWORD: String = System.getenv("SOCKS5_PASSWORD") ?: ""


    private var _PORT: Int = 1080

    init {
        val portStr = System.getenv("SERVER_PORT")
        if (portStr == null || portStr.isEmpty()) {
            log.info("SERVER_PORT is not set, use default port: {}", _PORT)
        } else {
            runCatching {
                _PORT = portStr.toInt()
            }.onFailure {
                log.error("SERVER_PORT is invalid: {}", portStr)
            }
        }
    }

}