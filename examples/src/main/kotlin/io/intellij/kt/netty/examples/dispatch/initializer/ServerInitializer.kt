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

        /*
        | type  | length  | body |
        | 1 byte | 4 byte | ... |
        type: 消息类型 1: HeartBeat 2: DataBody
         */
        p.addLast(DispatchDecoder()) // 接收客户端发送的消息，根据自定义的协议分发
        p.addLast(HeartBeatEncoder()) // 服务端 编码 HeartBeat 响应
        p.addLast(DataBodyEncoder())  // 服务端 编码 DataBody


        p.addLast(ServerHeartBeatHandler())
        p.addLast(ServerDataBodyHandler())

        p.addLast(LoginHandler()) // 回写消息需要 发送 DataBody
        p.addLast(LogoutHandler()) // 回写消息需要 发送 DataBody

    }

}