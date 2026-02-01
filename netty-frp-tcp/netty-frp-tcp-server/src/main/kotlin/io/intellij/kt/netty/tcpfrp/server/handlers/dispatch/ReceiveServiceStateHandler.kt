package io.intellij.kt.netty.tcpfrp.server.handlers.dispatch

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.tcpfrp.commons.Listeners
import io.intellij.kt.netty.tcpfrp.protocol.ConnState
import io.intellij.kt.netty.tcpfrp.protocol.channel.DispatchManager
import io.intellij.kt.netty.tcpfrp.protocol.channel.FrpChannel
import io.intellij.kt.netty.tcpfrp.protocol.client.ServiceState
import io.intellij.kt.netty.tcpfrp.protocol.server.UserState
import io.intellij.kt.netty.tcpfrp.server.listening.MultiPortsNettyServer
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

/**
 * ReceiveServiceStateHandler
 *
 * @author tech@intellij.io
 */
class ReceiveServiceStateHandler : SimpleChannelInboundHandler<ServiceState>() {

    companion object {
        private val log = getLogger(ReceiveServiceStateHandler::class.java)
    }

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, connState: ServiceState) {
        val frpChannel: FrpChannel = FrpChannel.getBy(ctx.channel())
        when (val serviceState: ConnState = ConnState.getByName(connState.stateName)) {

            ConnState.UNKNOWN -> throw IllegalStateException("unknown service state")


            ConnState.SUCCESS ->                 // frp-client ---> service 连接成功
                // 可以获取到 dispatchId
                frpChannel.write(
                    UserState.ready(connState.dispatchId),
                    Listeners.read(DispatchManager.getBy(frpChannel.getBy()).getChannel(connState.dispatchId)!!),
                    Listeners.read(frpChannel)
                )

            ConnState.FAILURE ->                 // frp-client ---> service 连接断开
                frpChannel.writeAndFlushEmpty()
                    .addListeners(
                        Listeners.releaseDispatchChannel(
                            DispatchManager.getBy(frpChannel.getBy()),
                            connState.dispatchId,
                            ConnState.FAILURE.desc
                        ),
                        Listeners.read(frpChannel)
                    )

            ConnState.BROKEN ->                 // service ---> frp-client 连接断开
                frpChannel.writeAndFlushEmpty()
                    .addListeners(
                        Listeners.releaseDispatchChannel(
                            DispatchManager.getBy(frpChannel.getBy()),
                            connState.dispatchId,
                            ConnState.BROKEN.desc
                        ),
                        Listeners.read(frpChannel)
                    )

            else -> log.error("ServiceConnStateHandler channelRead0 unknown state {}", serviceState)
        }
    }

    @Throws(Exception::class)
    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        FrpChannel.getBy(ctx.channel()).flush()
    }

    @Throws(Exception::class)
    override fun channelInactive(ctx: ChannelHandlerContext) {
        log.warn("stop multi port server")
        MultiPortsNettyServer.stopIn(ctx.channel())
    }

}
