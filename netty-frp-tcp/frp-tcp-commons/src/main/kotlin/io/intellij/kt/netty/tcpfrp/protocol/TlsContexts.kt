package io.intellij.kt.netty.tcpfrp.protocol

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.tcpfrp.SysConfig
import io.netty.handler.ssl.ClientAuth
import io.netty.handler.ssl.OpenSsl
import io.netty.handler.ssl.SslContext
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.SslProvider
import java.io.InputStream
import java.util.Objects


/**
 * SslContextUtils
 *
 * @author tech@intellij.io
 */
object TlsContexts {
    private val log = getLogger(TlsContexts::class.java)

    const val SERVER_CERT: String = "ssl/server/server.crt"
    const val SERVER_KEY: String = "ssl/server/server.key"
    const val CLIENT_CERT: String = "ssl/client/client.crt"
    const val CLIENT_KEY: String = "ssl/client/client.key"
    const val CA_CERT: String = "ssl/ca.crt"

    /**
     * Builds and returns an SSL context for the server if SSL is enabled in the configuration.
     * The method attempts to load server certificates, private key, and CA certificate to configure the SSL context.
     * If an error occurs during the process, it logs the error and returns null.
     *
     * @return an instance of [SslContext] configured for the server, or null if SSL is not enabled
     * or an error occurs during the setup.
     */
    fun buildServer(): SslContext? {
        if (SysConfig.get().enableSsl) {
            var cert: InputStream? = null
            var key: InputStream? = null
            var caCert: InputStream? = null
            try {
                cert = get(SERVER_CERT)
                key = get(SERVER_KEY)
                caCert = get(CA_CERT)
                val provider = if (OpenSsl.isAvailable()) SslProvider.OPENSSL else SslProvider.JDK
                return SslContextBuilder
                    .forServer(cert, key).trustManager(caCert)
                    .sslProvider(provider)
                    .clientAuth(ClientAuth.REQUIRE)
                    .build()
            } catch (e: Exception) {
                log.error("build server error", e)
                return null
            } finally {
                close(caCert, key, cert)
            }
        }
        return null
    }

    /**
     * Builds and returns an SSL context for a client connection if SSL is enabled in the system configuration.
     * The method attempts to load the client certificate, key, and CA certificate to construct the SSL context.
     * If an exception occurs during the process, it logs the error and returns null.
     *
     * @return the configured SslContext if SSL is enabled and configured correctly, or null if SSL is disabled
     * or an error occurs during SSL context setup.
     */
    fun buildClient(): SslContext? {
        if (SysConfig.get().enableSsl) {
            var cert: InputStream? = null
            var key: InputStream? = null
            var caCert: InputStream? = null
            try {
                cert = get(CLIENT_CERT)
                key = get(CLIENT_KEY)
                caCert = get(CA_CERT)
                val provider = if (OpenSsl.isAvailable()) SslProvider.OPENSSL else SslProvider.JDK
                return SslContextBuilder.forClient()
                    .keyManager(cert, key)
                    .sslProvider(provider)
                    .trustManager(caCert)
                    .build()
            } catch (e: Exception) {
                log.error("build server error", e)
                return null
            } finally {
                close(caCert, key, cert)
            }
        }
        return null
    }

    fun init(sysConfig: SysConfig) {
        try {
            val caCert = get(CA_CERT)
            val serverCrt = get(SERVER_CERT)
            val serverKey = get(SERVER_KEY)
            val clientCrt = get(CLIENT_CERT)
            val clientKey = get(CLIENT_KEY)

            if (Objects.nonNull(caCert)
                && Objects.nonNull(serverCrt) && Objects.nonNull(serverKey)
                && Objects.nonNull(clientCrt) && Objects.nonNull(clientKey)
            ) {
                // 待完善验证
                sysConfig.enableSsl = true
            }

            close(caCert, serverKey, serverCrt, clientKey, clientCrt)
        } catch (e: Exception) {
            log.error("init error", e)
        }
    }

    private fun get(path: String): InputStream? {
        return TlsContexts::class.java.getClassLoader().getResourceAsStream(path)
    }

    fun close(vararg inputStreams: InputStream?) {
        for (inputStream in inputStreams) {
            inputStream?.also {
                try {
                    it.close()
                } catch (e: Exception) {
                    log.error("inputStream close occurred error", e)
                }
            }
        }
    }

}