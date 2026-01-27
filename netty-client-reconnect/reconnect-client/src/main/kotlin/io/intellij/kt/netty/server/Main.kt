package io.intellij.kt.netty.server

import io.intellij.kt.netty.server.connector.ClientInitializer
import io.intellij.kt.netty.server.connector.Connector
import io.netty.channel.MultiThreadIoEventLoopGroup
import io.netty.channel.nio.NioIoHandler
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * ReconnectClient
 *
 * @author tech@intellij.io
 */
object ReconnectClient {

    /**
     * Creates a client; sends time‑varying messages periodically
     */
    @JvmStatic
    fun main(args: Array<String>) {
        val worker = MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory())
        val initializer = ::ClientInitializer

        val connector = Connector("127.0.0.1", 8082, worker, initializer).apply {
            this.connect()
        }

        worker.scheduleAtFixedRate(
            {
                UUID.randomUUID()
                    .toString()
                    .also(connector::writeAndFlush)
            },
            1, 3, TimeUnit.SECONDS
        )
    }

}
