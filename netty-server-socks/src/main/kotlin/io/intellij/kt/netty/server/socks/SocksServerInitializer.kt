package io.intellij.kt.netty.server.socks

import io.intellij.kt.netty.server.socks.handlers.SocksServerHandler
import io.intellij.kt.netty.server.socks.handlers.socks5auth.Authenticator
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.socksx.SocksPortUnificationServerHandler

/**
 * SocksServerInitializer
 *
 * @author tech@intellij.io
 */
class SocksServerInitializer(
    val authenticator: Authenticator
) : ChannelInitializer<SocketChannel>() {
    @Throws(Exception::class)
    override fun initChannel(ch: SocketChannel) {
        val p = ch.pipeline()
        p.addLast(SocksPortUnificationServerHandler())
        p.addLast(SocksServerHandler.getInstance(authenticator))
    }
}
