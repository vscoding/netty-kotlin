package io.intellij.kt.netty.tcpfrp.client.service

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.tcpfrp.commons.Listeners
import io.intellij.kt.netty.tcpfrp.protocol.channel.DispatchManager
import io.intellij.kt.netty.tcpfrp.protocol.channel.DispatchPacket
import io.intellij.kt.netty.tcpfrp.protocol.channel.FrpChannel
import io.intellij.kt.netty.tcpfrp.protocol.client.ServiceState
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

/**
 * ServiceChannelHandler
 *
 * @author tech@intellij.io
 */
class ServiceChannelHandler(
    private val serviceName: String,
    private val dispatchId: String,
    private val frpChannel: FrpChannel,
    private val dispatchManager: DispatchManager
) : ChannelInboundHandlerAdapter() {

    companion object {
        private val log = getLogger(ServiceChannelHandler::class.java)
    }

    /**
     * 服务连接成功
     */
    @Throws(Exception::class)
    override fun channelActive(ctx: ChannelHandlerContext) {
        log.info("[SERVICE] 建立服务端连接 |dispatchId={}|serviceName={}", dispatchId, serviceName)
        dispatchManager.addChannel(dispatchId, ctx.channel())
        // BootStrap set AUTO_READ=false
        // 等待frp-server 发送 UserConnState(READY)
    }

    /**
     * 读取到服务的数据
     */
    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        if (msg is ByteBuf) {
            log.debug(
                "接收到服务端的数据 |dispatchId={}|serviceName={}|len={}",
                dispatchId,
                serviceName,
                msg.readableBytes()
            )
            frpChannel.write(DispatchPacket.create(dispatchId, msg))
                .addListeners(Listeners.read(frpChannel))
            return
        }
        log.error("ServiceHandler channelRead error, msg: {}", msg)
        throw IllegalArgumentException("msg is not ByteBuf")
    }

    @Throws(Exception::class)
    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.read()
        frpChannel.flush()
    }

    /**
     * 服务连接断开
     *
     *
     * 1. 通知 frp-server，服务连接断开
     * 2. 关闭服务的 channel
     */
    @Throws(Exception::class)
    override fun channelInactive(ctx: ChannelHandlerContext) {
        log.warn("[SERVICE] 丢失服务端连接 |dispatchId={}|serviceName{}", dispatchId, serviceName)
        // frp-client -x-> mysql:3306
        frpChannel.writeAndFlush(ServiceState.broken(dispatchId), Listeners.read(frpChannel))
        ctx.close()
    }

    @Throws(Exception::class)
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable?) {
        log.error("exception caught|dispatchId={}", dispatchId, cause)
        ctx.close()
    }
}
