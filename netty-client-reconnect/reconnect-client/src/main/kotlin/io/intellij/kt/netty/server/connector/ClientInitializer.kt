package io.intellij.kt.netty.server.connector

import io.intellij.kt.netty.commons.getLogger
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.FixedLengthFrameDecoder
import io.netty.handler.codec.string.StringDecoder
import io.netty.handler.codec.string.StringEncoder
import java.util.UUID

/**
 * ClientInitializer
 *
 * @author tech@intellij.io
 */
class ClientInitializer : ChannelInitializer<Channel>() {
    companion object {
        private val UUID_LENGTH = UUID.randomUUID().toString().length
    }

    @Throws(Exception::class)
    override fun initChannel(ch: Channel) {
        val p = ch.pipeline()
        p.addLast(FixedLengthFrameDecoder(UUID_LENGTH))
        p.addLast(StringDecoder()).addLast(StringEncoder())
        p.addLast(ClientHandler())
    }

}

class ClientHandler : SimpleChannelInboundHandler<String>() {
    companion object {
        private val log = getLogger(ClientHandler::class.java)
    }

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: String) {
        log.info("read  msg|{}", msg)
    }

    @Throws(Exception::class)
    override fun channelInactive(ctx: ChannelHandlerContext) {
        log.warn("lost connection, reconnecting...")
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        log.error("exceptionCaught|{}", cause.message)
        ctx.close()
    }

}