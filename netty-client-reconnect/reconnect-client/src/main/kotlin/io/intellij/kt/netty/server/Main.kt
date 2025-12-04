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
fun main() {
    val worker = MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory())

    val connector = Connector("127.0.0.1", 8082, worker, ClientInitializer())
        .apply { connect() }

    worker.scheduleAtFixedRate(
        {
            UUID.randomUUID()
                .toString()
                .also(connector::writeAndFlush)
        },
        1, 3, TimeUnit.SECONDS
    )
}
