package io.intellij.kotlin.netty.server

import io.intellij.kotlin.netty.commons.getLogger
import io.intellij.kotlin.netty.server.connector.ClientInitializer
import io.intellij.kotlin.netty.server.connector.Connector
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * ReconnectClient
 *
 * @author tech@intellij.io
 */
private val log = getLogger("ReconnectClient")

fun main() {
    val ses = Executors.newSingleThreadScheduledExecutor()

    val connector = Connector("127.0.0.1", 8082, { bootstrap ->
        val worker = NioEventLoopGroup(1)
        bootstrap.group(worker)
            .channel(NioSocketChannel::class.java)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .handler(ClientInitializer())
    })

    connector.connect()

    ses.scheduleAtFixedRate({
        connector.channel?.also {
            if (it.isActive) {
                val uuid: String = UUID.randomUUID().toString()
                log.info("write msg|{}", uuid)
                it.writeAndFlush(uuid)
            }
        }
    }, 1, 3, TimeUnit.SECONDS)

}