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

        /*
        | type  | length  | body |
        | 1 byte | 4 byte | ... |
        type: 消息类型 1: HeartBeat 2: DataBody
         */
        p.addLast(DispatchDecoder()) // 接收服务端返回的消息，根据自定义的协议

        p.addLast(HeartBeatEncoder()) // 处理 客户端 发送 HeartBeat
        p.addLast(DataBodyEncoder())  // 处理 客户端 发送 DataBody

        p.addLast(LoginReqEncoder())  // 处理 客户端 发送 LoginReq 转变成 DataBody 由 DataBodyEncoder 处理

        p.addLast(ClientHeartBeatHandler())
        p.addLast(ClientDataBodyHandler())

        p.addLast(ResponseLogHandler())

    }

}