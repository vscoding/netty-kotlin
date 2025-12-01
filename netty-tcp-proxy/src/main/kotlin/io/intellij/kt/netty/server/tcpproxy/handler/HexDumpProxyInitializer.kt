package io.intellij.kt.netty.server.tcpproxy.handler

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler

/**
 * HexDumpProxyInitializer
 *
 * @author tech@intellij.io
 */
class HexDumpProxyInitializer(
    val remoteHost: String,
    val remotePort: Int
) : ChannelInitializer<SocketChannel>() {

    @Throws(Exception::class)
    override fun initChannel(ch: SocketChannel) {
        val p = ch.pipeline()
        p.addLast(LoggingHandler(LogLevel.INFO))
            .addLast(HexDumpProxyFrontendHandler(remoteHost, remotePort))
    }
}
