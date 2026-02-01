package io.intellij.kt.netty.tcpfrp.config

import com.alibaba.fastjson2.JSONArray
import com.alibaba.fastjson2.JSONPath
import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.tcpfrp.SysConfig
import io.intellij.kt.netty.tcpfrp.protocol.TlsContexts
import io.intellij.kt.netty.tcpfrp.protocol.client.ListeningConfig
import io.netty.handler.ssl.SslContext
import org.apache.commons.io.IOUtils
import java.io.InputStream
import java.util.Objects

/**
 * ClientConfig
 *
 * @author tech@intellij.io
 */
data class ClientConfig(
    val valid: Boolean,
    val serverHost: String = "",
    val serverPort: Int = 0,
    val authToken: String = "",
    val listeningConfigMap: Map<String, ListeningConfig> = emptyMap(),
    val enableSSL: Boolean = false,
    val sslContext: SslContext? = null
) {
    companion object {
        private val log = getLogger(ClientConfig::class.java)
        private val INVALID_CONFIG: ClientConfig = ClientConfig(false)


        fun init(inputStream: InputStream?): ClientConfig {
            try {
                if (inputStream == null) {
                    return INVALID_CONFIG
                }

                val json: String = IOUtils.readLines(inputStream, Charsets.UTF_8)
                    .joinToString(separator = "")

                val evalServerHost = JSONPath.eval(json, "$.server.host") as String?
                val evalServerPort = JSONPath.eval(json, "$.server.port") as Int?
                val evalAuthToken = JSONPath.eval(json, "$.server.auth.token") as String?

                if (evalServerHost == null || evalServerPort == null || evalAuthToken == null) {
                    return INVALID_CONFIG
                }

                val array = JSONPath.eval(json, "$.clients") as JSONArray
                val map = HashMap<String, ListeningConfig>()
                if (array.isEmpty()) {
                    return INVALID_CONFIG
                } else {
                    for (i in array.indices) {
                        val name = JSONPath.eval(json, "$.clients[$i].name") as String?
                        val localIp = JSONPath.eval(json, "$.clients[$i].local_ip") as String?
                        val localPort = JSONPath.eval(json, "$.clients[$i].local_port") as Int?
                        val remotePort = JSONPath.eval(json, "$.clients[$i].remote_port") as Int?

                        if (name == null || localIp == null || localPort == null || remotePort == null) {
                            return INVALID_CONFIG
                        }

                        map[name] = ListeningConfig(name, localIp, localPort, remotePort)
                    }

                    return ClientConfig(
                        valid = true,
                        serverHost = evalServerHost,
                        serverPort = evalServerPort,
                        authToken = evalAuthToken,
                        listeningConfigMap = map,
                        enableSSL = SysConfig.get().enableSsl,
                        sslContext = TlsContexts.buildClient()
                    )
                }
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

        fun loadConfig(path: String): ClientConfig {
            val clientConfig: ClientConfig = init(ClientConfig::class.java.getClassLoader().getResourceAsStream(path))
            if (clientConfig.valid) {
                log.info("client config|{}", clientConfig)
                SysConfig.get().logDetails()
            }
            return clientConfig
        }
    }

    fun then(consumer: (ClientConfig) -> Unit) {
        if (valid) {
            consumer(this)
        }
    }

    override fun toString(): String {
        return "ClientConfig(valid=$valid, serverHost='$serverHost', serverPort=$serverPort, authToken='$authToken', listeningConfigMap=$listeningConfigMap)"
    }

}