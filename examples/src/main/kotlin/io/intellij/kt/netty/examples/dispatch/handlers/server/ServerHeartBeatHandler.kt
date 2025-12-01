package io.intellij.kt.netty.examples.dispatch.handlers.server

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.examples.dispatch.model.HeartBeat
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import java.util.Date

/**
 * ServerHeartBeatHandler
 *
 * @author tech@intellij.io
 */
class ServerHeartBeatHandler : SimpleChannelInboundHandler<HeartBeat>() {

    companion object {
        private val log = getLogger(ServerHeartBeatHandler::class.java)
    }

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: HeartBeat) {
        log.info("receive client heart beat|{}", msg)
        val hbResponse = HeartBeat(Date(), msg.id, msg.seq + 1)
        log.info("send server heart beat|{}", hbResponse)
        ctx.writeAndFlush(hbResponse)
    }

}
