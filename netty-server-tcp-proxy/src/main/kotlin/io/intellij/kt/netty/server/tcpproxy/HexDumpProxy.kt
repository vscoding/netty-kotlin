package io.intellij.kt.netty.server.tcpproxy

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.server.tcpproxy.handler.HexDumpProxyInitializer
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler

/**
 * HexDumpProxy
 *
 * @author tech@intellij.io
 */
object HexDumpProxy {
    private val log = getLogger("HexDumpProxy")

    @JvmStatic
    fun main(args: Array<String>) {
        val config = loadConfig()
        log.info("config:{}", config)
        if (!config.valid) {
            return
        }

        log.info("Proxying *:{} to {}:{}", config.localPort, config.remoteHost, config.remotePort)

        val boss = NioEventLoopGroup(1)
        val worker = NioEventLoopGroup()

        val b = ServerBootstrap()
            .apply {
                this.group(boss, worker)
                    .channel(NioServerSocketChannel::class.java)
                    .handler(LoggingHandler(LogLevel.INFO))
                    .childOption(ChannelOption.AUTO_READ, false)
            }.apply {
                this.childHandler(HexDumpProxyInitializer(config.remoteHost, config.remotePort))
            }

        runCatching {
            val sync = b.bind(config.localPort).sync()
            log.info("Proxy server started on port {}", config.localPort)
            sync.channel().closeFuture().sync()
        }.onFailure { e ->
            log.error("Proxy server start failed: {}", e.message, e)
        }.also {
            boss.shutdownGracefully()
            worker.shutdownGracefully()
        }

    }

    data class TcpProxyConfig(
        val valid: Boolean = false,
        val localHost: String = "",
        val localPort: Int = -1,
        val remoteHost: String = "",
        val remotePort: Int = -1
    ) {
        override fun toString(): String {
            return if (valid) {
                "TcpProxyConfig(localHost='$localHost', localPort=$localPort, remoteHost='$remoteHost', remotePort=$remotePort)"
            } else {
                "TcpProxyConfig(INVALID)"
            }
        }
    }

    private fun loadConfig(): TcpProxyConfig =
        run {
            val localPort = System.getenv("LOCAL_PORT")?.toIntOrNull()
            val remoteHost = System.getenv("REMOTE_HOST")
            val remotePort = System.getenv("REMOTE_PORT")?.toIntOrNull()

            Triple(localPort, remoteHost, remotePort)
        }.takeIf { (localPort, remoteHost, remotePort) ->
            localPort != null && !remoteHost.isNullOrBlank() && remotePort != null
        }?.let { (localPort, remoteHost, remotePort) ->
            TcpProxyConfig(
                valid = true,
                localHost = "0.0.0.0",
                localPort = localPort!!,
                remoteHost = remoteHost!!,
                remotePort = remotePort!!
            )
        } ?: TcpProxyConfig().also {
            log.error("Missing required environment variables: LOCAL_PORT, REMOTE_HOST, REMOTE_PORT")
        }

}
