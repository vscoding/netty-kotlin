package io.intellij.kt.netty.examples.dispatch.handlers.client

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.examples.dispatch.model.HeartBeat
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

/**
 * ClientHeartBeatHandler
 *
 * @author tech@intellij.io
 */
class ClientHeartBeatHandler : SimpleChannelInboundHandler<HeartBeat>() {
    companion object {
        private val log = getLogger(ClientHeartBeatHandler::class.java)
    }

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext?, msg: HeartBeat) {
        log.info("receive server heart beat|{}", msg)
    }
}
