package io.intellij.kt.netty.tcpfrp.server.handlers

import io.intellij.kt.netty.tcpfrp.config.ServerConfig
import io.intellij.kt.netty.tcpfrp.protocol.channel.FrpChannelInitializer
import io.intellij.kt.netty.tcpfrp.protocol.codec.FrpCodec
import io.intellij.kt.netty.tcpfrp.server.handlers.initial.AuthRequestHandler
import io.netty.channel.socket.SocketChannel

/**
 * FrpServerInitializer
 *
 * @author tech@intellij.io
 */
class FrpServerInitializer(
    val config: ServerConfig
) : FrpChannelInitializer() {

    @Throws(Exception::class)
    override fun initChannel0(ch: SocketChannel) {
        val pipeline = ch.pipeline()

        if (config.enableSSL) {
            pipeline.addLast(
                config.sslContext!!.newHandler(ch.alloc())
            )
        }

        pipeline.addLast(FrpCodec.serverDecoder())
            .addLast(FrpCodec.basicMsgEncoder())
            .addLast(FrpCodec.dispatchEncoder())


        pipeline.addLast(AuthRequestHandler(config.authToken))
    }
}
