package io.intellij.kt.netty.examples.dispatch.initializer

import io.intellij.kt.netty.examples.dispatch.codec.DispatchDecoder
import io.intellij.kt.netty.examples.dispatch.codec.encoders.DataBodyEncoder
import io.intellij.kt.netty.examples.dispatch.codec.encoders.HeartBeatEncoder
import io.intellij.kt.netty.examples.dispatch.codec.encoders.LoginReqEncoder
import io.intellij.kt.netty.examples.dispatch.handlers.client.ClientDataBodyHandler
import io.intellij.kt.netty.examples.dispatch.handlers.client.ClientHeartBeatHandler
import io.intellij.kt.netty.examples.dispatch.handlers.client.ResponseLogHandler
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel

/**
 * ClientInitializer
 *
 * @author tech@intellij.io
 */
class ClientInitializer : ChannelInitializer<SocketChannel>() {

    @Throws(Exception::class)
    override fun initChannel(ch: SocketChannel) {
        val p = ch.pipeline()

        p.addLast(DispatchDecoder())
        p.addLast(HeartBeatEncoder())
        p.addLast(DataBodyEncoder())

        p.addLast(LoginReqEncoder())

        p.addLast(ClientHeartBeatHandler())
        p.addLast(ClientDataBodyHandler())

        p.addLast(ResponseLogHandler())

    }

}