package io.intellij.kt.netty.tcpfrp.config

import com.alibaba.fastjson2.JSONPath
import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.tcpfrp.SysConfig
import io.intellij.kt.netty.tcpfrp.protocol.SslContextUtils
import io.netty.handler.ssl.SslContext
import org.apache.commons.io.IOUtils
import java.io.InputStream
import java.util.Objects

/**
 * ServerConfig
 *
 * @author tech@intellij.io
 */
data class ServerConfig(
    val valid: Boolean,
    val host: String = "",
    val port: Int = 0,
    val authToken: String = "",
    val enableSSL: Boolean = false,
    val sslContext: SslContext? = null
) {

    companion object {
        private val log = getLogger(ServerConfig::class.java)

        private val INVALID_CONFIG: ServerConfig = ServerConfig(false)

        fun init(inputStream: InputStream?): ServerConfig {
            try {
                if (inputStream == null) {
                    return INVALID_CONFIG
                }

                val json = IOUtils.readLines(inputStream, "UTF-8").joinToString(separator = "")

                val host = JSONPath.eval(json, "$.server.host") as String?
                val port = JSONPath.eval(json, "$.server.port") as Int?
                val authToken = JSONPath.eval(json, "$.server.auth.token") as String?

                if (host == null || port == null || authToken == null) {
                    return INVALID_CONFIG
                }

                return ServerConfig(
                    true, host, port, authToken,
                    SysConfig.get().enableSsl, SslContextUtils.buildServer()
                )

            } catch (e: Exception) {
                log.error(e.message)
                return INVALID_CONFIG
            } finally {
                if (Objects.nonNull(inputStream)) {
                    try {
                        inputStream!!.close()
                    } catch (e: Exception) {
                        log.error(e.message)
                    }
                }
            }
        }

        fun loadConfig(path: String): ServerConfig {
            val serverConfig = init(ServerConfig::class.java.getClassLoader().getResourceAsStream(path))
            if (serverConfig.valid) {
                log.info("server config|{}", serverConfig)
                SysConfig.get().logDetails()
            }
            return serverConfig
        }

    }

    fun then(consumer: (ServerConfig) -> Unit) {
        if (this.valid) {
            consumer(this)
        } else {
            log.error("server config is invalid")
        }
    }
}