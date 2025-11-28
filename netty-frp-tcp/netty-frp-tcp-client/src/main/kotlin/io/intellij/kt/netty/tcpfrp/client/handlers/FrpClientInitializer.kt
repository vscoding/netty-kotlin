package io.intellij.kt.netty.tcpfrp.client.handlers

import io.intellij.kt.netty.tcpfrp.client.handlers.initial.AuthResponseHandler
import io.intellij.kt.netty.tcpfrp.config.ClientConfig
import io.intellij.kt.netty.tcpfrp.protocol.channel.FrpChannelInitializer
import io.intellij.kt.netty.tcpfrp.protocol.codec.FrpCodec
import io.netty.channel.socket.SocketChannel

/**
 * FrpClientInitializer
 *
 * @author tech@intellij.io
 */
class FrpClientInitializer(private val clientConfig: ClientConfig) : FrpChannelInitializer() {

    override fun initChannel0(ch: SocketChannel) {
        val p = ch.pipeline()
        if (clientConfig.enableSSL) {
            p.addLast(clientConfig.sslContext!!.newHandler(ch.alloc()))
        }

        p.addLast(FrpCodec.clientDecoder())
            .addLast(FrpCodec.basicMsgEncoder())
            .addLast(FrpCodec.dispatchEncoder())

        p.addLast(AuthResponseHandler(clientConfig.listeningConfigMap))

    }

}