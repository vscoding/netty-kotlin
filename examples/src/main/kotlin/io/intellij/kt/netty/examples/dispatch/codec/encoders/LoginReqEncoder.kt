package io.intellij.kt.netty.examples.dispatch.codec.encoders

import io.intellij.kt.netty.examples.dispatch.model.msg.LoginReq
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageEncoder

/**
 * LoginReqEncoder
 *
 * @author tech@intellij.io
 */
class LoginReqEncoder : MessageToMessageEncoder<LoginReq>() {

    @Throws(Exception::class)
    override fun encode(channelHandlerContext: ChannelHandlerContext, loginReq: LoginReq, list: MutableList<Any>) {
        list.add(loginReq.toDataBody())
    }

}
