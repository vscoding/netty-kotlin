package io.intellij.kt.netty.tcpfrp.server.listening

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.tcpfrp.commons.Listeners
import io.intellij.kt.netty.tcpfrp.protocol.channel.DispatchIdUtils
import io.intellij.kt.netty.tcpfrp.protocol.channel.DispatchManager
import io.intellij.kt.netty.tcpfrp.protocol.channel.DispatchPacket
import io.intellij.kt.netty.tcpfrp.protocol.channel.FrpChannel
import io.intellij.kt.netty.tcpfrp.protocol.server.UserState
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

/**
 * UserChannelHandler
 *
 * @author tech@intellij.io
 */
class UserChannelHandler(
    private val listeningPort: Int,
    private val frpChannel: FrpChannel
) : ChannelInboundHandlerAdapter() {

    companion object {
        private val log = getLogger(UserChannelHandler::class.java)
    }

    /**
     * 用户连接成功
     */
    @Throws(Exception::class)
    override fun channelActive(ctx: ChannelHandlerContext) {
        // e.g. user ---> frp-server:3306
        val dispatchId: String = DispatchIdUtils.getDispatchId(ctx.channel())

        DispatchManager.getFromCh(frpChannel.ch).addChannel(dispatchId, ctx.channel())

        log.info("[USER] 用户建立了连接 |dispatchId={}|port={}", dispatchId, this.listeningPort)

        // 等待frp-client 发送 ServiceConnState(SUCCESS),然后READ
        // AUTO_READ = false
        frpChannel.writeAndFlush(UserState.accept(dispatchId, this.listeningPort))
            .addListeners(Listeners.read(frpChannel))
    }

    /**
     * 用户发送数据
     *
     * after [io.intellij.kt.netty.tcpfrp.commons.Listeners.read]
     */
    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg is ByteBuf) {
            val dispatchId: String = DispatchIdUtils.getDispatchId(ctx.channel())
            log.debug(
                "接收到用户的数据 |dispatchId={}|port={}|len={}",
                dispatchId,
                this.listeningPort,
                msg.readableBytes()
            )
            frpChannel.writeAndFlush(
                DispatchPacket.create(dispatchId, msg),
                { f ->
                    if (f.isSuccess) {
                        ctx.read()
                    }
                },
                { f ->
                    if (f.isSuccess) {
                        frpChannel.read()
                    }
                }
            )
            return
        }
        log.warn("unknown msg type|{}", msg.javaClass)
        throw RuntimeException("unknown msg type")
    }

    /**
     * 用户断开连接
     */
    @Throws(Exception::class)
    override fun channelInactive(ctx: ChannelHandlerContext) {
        val dispatchId: String = DispatchIdUtils.getDispatchId(ctx.channel())
        log.warn("[USER] 用户断开了连接 |dispatchId={}", dispatchId)
        frpChannel.writeAndFlush(UserState.broken(dispatchId), Listeners.read(frpChannel))
    }

    @Throws(Exception::class)
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        log.error("exception caught|{}", cause.message, cause)
        ctx.close()
    }

}