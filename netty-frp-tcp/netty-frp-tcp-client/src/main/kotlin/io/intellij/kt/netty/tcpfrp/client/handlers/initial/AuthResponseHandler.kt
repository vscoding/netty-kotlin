package io.intellij.kt.netty.tcpfrp.client.handlers.initial

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.tcpfrp.protocol.channel.FrpChannel
import io.intellij.kt.netty.tcpfrp.protocol.client.ListeningConfig
import io.intellij.kt.netty.tcpfrp.protocol.client.ListeningRequest
import io.intellij.kt.netty.tcpfrp.protocol.server.AuthResponse
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

/**
 * AuthResponseHandler
 *
 * @author tech@intellij.io
 */
class AuthResponseHandler(
    val configMap: Map<String, ListeningConfig>
) : SimpleChannelInboundHandler<AuthResponse>() {

    companion object {
        private val log = getLogger(AuthResponseHandler::class.java)
    }

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, authResponse: AuthResponse) {
        val frpChannel: FrpChannel = FrpChannel.getBy(ctx.channel())
        if (authResponse.success) {
            log.info("authenticate success")
            val listeningPorts: List<Int> = configMap.mapValues { (_, v) -> v.remotePort }.values.toList()
            log.info("send listening request, ports: {}", listeningPorts)
            frpChannel.write(
                ListeningRequest.create(listeningPorts),
                { channelFuture ->
                    if (channelFuture.isSuccess) {
                        val p = ctx.pipeline()
                        p.addLast(ListeningResponseHandler(configMap))
                        p.remove(this)
                        // ReceiveUserStateHandler
                        p.fireChannelActive()
                    }
                }
            )
        } else {
            // 认证失败，服务端会主动关闭连接
            log.warn("authenticate failed")
            frpChannel.close()
        }
    }

    @Throws(Exception::class)
    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        FrpChannel.getBy(ctx.channel()).flush()
    }
}
