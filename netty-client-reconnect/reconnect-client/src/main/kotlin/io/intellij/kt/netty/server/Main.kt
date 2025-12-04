package io.intellij.kt.netty.server

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.server.connector.ClientInitializer
import io.intellij.kt.netty.server.connector.Connector
import io.netty.channel.nio.NioEventLoopGroup
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * ReconnectClient
 *
 * @author tech@intellij.io
 */
private val log = getLogger("ReconnectClient")

fun main() {
    val worker = NioEventLoopGroup(1)

    val connector = Connector("127.0.0.1", 8082, worker, ClientInitializer())

    connector.connect()

    worker.scheduleAtFixedRate({
        connector.channel?.also {
            if (it.isActive) {
                val uuid: String = UUID.randomUUID().toString()
                log.info("write msg|{}", uuid)
                it.writeAndFlush(uuid)
            }
        }
    }, 1, 3, TimeUnit.SECONDS)

}