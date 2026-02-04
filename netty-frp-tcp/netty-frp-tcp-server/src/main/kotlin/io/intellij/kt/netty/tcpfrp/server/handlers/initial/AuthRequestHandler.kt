package io.intellij.kt.netty.tcpfrp.server.handlers.initial

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.tcpfrp.protocol.channel.FrpChannel
import io.intellij.kt.netty.tcpfrp.protocol.channel.getFrpChannel
import io.intellij.kt.netty.tcpfrp.protocol.client.AuthRequest
import io.intellij.kt.netty.tcpfrp.protocol.server.AuthResponse
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

/**
 * AuthRequestHandler
 *
 * @author tech@intellij.io
 */
class AuthRequestHandler(
    val configToken: String
) : ChannelInboundHandlerAdapter() {

    companion object {
        private val log = getLogger(AuthRequestHandler::class.java)
    }

    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any?) {
        val frpChannel: FrpChannel = ctx.channel().getFrpChannel()
        if (msg is AuthRequest) {
            if (authenticate(msg)) {
                frpChannel.write(
                    AuthResponse.success(),
                    { channelFuture ->
                        if (channelFuture.isSuccess) {
                            val p = ctx.pipeline()
                            p.addLast(ListeningRequestHandler())
                            p.remove(this)
                            p.fireChannelActive()
                        } else {
                            frpChannel.close()
                        }
                    })
            } else {
                frpChannel.write(AuthResponse.failure(), ChannelFutureListener.CLOSE)
            }
        } else {
            // 第一个消息不是认证消息，关闭连接
            frpChannel.close()
        }
    }

    @Throws(Exception::class)
    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.channel().getFrpChannel().flush()
    }

    private fun authenticate(authRequest: AuthRequest): Boolean {
        val authResult = authRequest.token.equals(configToken)
        if (authResult) {
            log.info("authenticate client success")
        } else {
            log.error("authenticate client failed")
        }
        return authResult
    }
}
