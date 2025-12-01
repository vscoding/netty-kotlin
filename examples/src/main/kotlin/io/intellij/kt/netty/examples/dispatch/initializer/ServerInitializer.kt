package io.intellij.kt.netty.examples.dispatch.initializer

import io.intellij.kt.netty.examples.dispatch.codec.DispatchDecoder
import io.intellij.kt.netty.examples.dispatch.codec.encoders.DataBodyEncoder
import io.intellij.kt.netty.examples.dispatch.codec.encoders.HeartBeatEncoder
import io.intellij.kt.netty.examples.dispatch.handlers.ByteCountingHandler
import io.intellij.kt.netty.examples.dispatch.handlers.server.LoginHandler
import io.intellij.kt.netty.examples.dispatch.handlers.server.LogoutHandler
import io.intellij.kt.netty.examples.dispatch.handlers.server.ServerDataBodyHandler
import io.intellij.kt.netty.examples.dispatch.handlers.server.ServerHeartBeatHandler
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel

/**
 * ServerInitializer
 *
 * @author tech@intellij.io
 */
class ServerInitializer : ChannelInitializer<SocketChannel>() {

    @Throws(Exception::class)
    override fun initChannel(ch: SocketChannel) {
        val p = ch.pipeline()
        p.addLast(ByteCountingHandler())

        p.addLast(DispatchDecoder())
        p.addLast(HeartBeatEncoder())
        p.addLast(DataBodyEncoder())


        p.addLast(ServerHeartBeatHandler())
        p.addLast(ServerDataBodyHandler())

        p.addLast(LoginHandler())
        p.addLast(LogoutHandler())

    }

}