package io.intellij.kt.netty.tcpfrp.server.handlers.initial

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.tcpfrp.protocol.FrpBasicMsg
import io.intellij.kt.netty.tcpfrp.protocol.channel.DispatchManager
import io.intellij.kt.netty.tcpfrp.protocol.channel.FrpChannel
import io.intellij.kt.netty.tcpfrp.protocol.client.ListeningRequest
import io.intellij.kt.netty.tcpfrp.protocol.server.ListeningResponse
import io.intellij.kt.netty.tcpfrp.server.handlers.dispatch.DispatchToUserHandler
import io.intellij.kt.netty.tcpfrp.server.handlers.dispatch.ReceiveServiceStateHandler
import io.intellij.kt.netty.tcpfrp.server.listening.MultiPortsNettyServer
import io.intellij.kt.netty.tcpfrp.server.listening.MultiPortsTestUtils
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

/**
 * ListeningRequestHandler
 *
 * @author tech@intellij.io
 */
class ListeningRequestHandler : SimpleChannelInboundHandler<ListeningRequest>() {

    companion object {
        private val log = getLogger(ListeningRequestHandler::class.java)
    }

    /**
     * Triggered from [AuthRequestHandler]
     */
    @Throws(Exception::class)
    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.read()
    }

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, listeningRequest: ListeningRequest) {
        val frpChannel: FrpChannel = FrpChannel.getBy(ctx.channel())

        log.info("get listening request: {}", listeningRequest)
        val listeningPorts: List<Int> = listeningRequest.listeningPorts
        // 测试可以监听
        val test: ListeningResponse = MultiPortsTestUtils.test(listeningPorts)

        if (test.success) {
            val server = MultiPortsNettyServer(listeningPorts, frpChannel)
            if (server.start()) {
                frpChannel.write(FrpBasicMsg.buildListeningResponse(test)).addListener(
                    ChannelFutureListener { f: ChannelFuture ->
                        if (f.isSuccess) {
                            // remote this
                            val p = ctx.pipeline()
                            p.remove(this)
                            log.info("init MultiPortNettyServer")
                            MultiPortsNettyServer.buildIn(frpChannel.getBy(), server)

                            log.info("init DispatchManager")
                            DispatchManager.buildIn(frpChannel.getBy())

                            p.addLast(PingHandler())
                                .addLast(ReceiveServiceStateHandler())
                                .addLast(DispatchToUserHandler())

                            log.info("ListeningRequestHandler channelRead0|fireChannelActive")
                            p.fireChannelActive()
                        } else {
                            frpChannel.close()
                        }
                    }
                )
            } else {
                log.error("start multi port netty server failed")
                val newRt = ListeningResponse(
                    false,
                    test.listeningStatus,
                    "start multi port netty server failed",
                )
                frpChannel.write(FrpBasicMsg.buildListeningResponse(newRt))
                    .addListener(ChannelFutureListener.CLOSE)
            }
        } else {
            frpChannel.write(FrpBasicMsg.buildListeningResponse(test))
                .addListener(ChannelFutureListener.CLOSE)
        }
    }

    @Throws(Exception::class)
    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.flush()
    }
}
