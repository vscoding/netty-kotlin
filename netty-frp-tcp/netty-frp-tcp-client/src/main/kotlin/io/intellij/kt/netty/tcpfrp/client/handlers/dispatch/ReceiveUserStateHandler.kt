package io.intellij.kt.netty.tcpfrp.client.handlers.dispatch

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.tcpfrp.client.service.DirectServiceHandler
import io.intellij.kt.netty.tcpfrp.client.service.ServiceChannelHandler
import io.intellij.kt.netty.tcpfrp.commons.Listeners
import io.intellij.kt.netty.tcpfrp.protocol.ConnState
import io.intellij.kt.netty.tcpfrp.protocol.channel.DispatchManager
import io.intellij.kt.netty.tcpfrp.protocol.channel.FrpChannel
import io.intellij.kt.netty.tcpfrp.protocol.client.ListeningConfig
import io.intellij.kt.netty.tcpfrp.protocol.client.ServiceState
import io.intellij.kt.netty.tcpfrp.protocol.server.UserState
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelOption
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.FutureListener
import io.netty.util.concurrent.Promise

/**
 * ReceiveUserStateHandler
 *
 * @author tech@intellij.io
 */
/**
 * ReceiveUserStateHandler
 *
 * 对应 Java 版 ReceiveUserStateHandler 的 Kotlin 实现
 */
class ReceiveUserStateHandler(
    configMap: Map<String, ListeningConfig>
) : SimpleChannelInboundHandler<UserState>() {

    companion object {
        private val log = getLogger(ReceiveUserStateHandler::class.java)
    }

    private val portToConfig: Map<Int, ListeningConfig> =
        configMap.values.associateBy { it.remotePort }

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, connState: UserState) {
        val frpChannel = FrpChannel.getBy(ctx.channel())
        when (val userState = ConnState.getByName(connState.stateName)) {

            ConnState.UNKNOWN -> {
                log.error("channelRead0 unknown state : {}", connState.stateName)
                frpChannel.close()
                return
            }

            // accept connection ：user ---> frp-server:3306
            ConnState.ACCEPT -> {
                val serviceChannelPromise: Promise<Channel> = ctx.executor().newPromise()
                val dispatchId = connState.dispatchId
                val listeningPort = connState.listeningPort
                val config = portToConfig[listeningPort]

                if (config == null) {
                    log.warn("[ACCEPT] 未找到监听配置 |port={}", listeningPort)
                    frpChannel.writeAndFlush(ServiceState.failure(dispatchId))
                        .addListeners(Listeners.read(frpChannel))
                    return
                }

                log.info("[ACCEPT] 接收到用户连接 |dispatchId={}|name={}", dispatchId, config.name)

                serviceChannelPromise.addListener(FutureListener<Channel> { future: Future<Channel> ->
                    val serviceChannel = future.now
                    if (future.isSuccess) {
                        log.info(
                            "[ACCEPT] 接收到用户连接后，服务连接创建成功|dispatchId={}|name={}",
                            dispatchId, config.name
                        )
                        val servicePipeline = serviceChannel.pipeline()
                        servicePipeline.addLast(
                            ServiceChannelHandler(
                                serviceName = config.name,
                                dispatchId,
                                frpChannel,
                                DispatchManager.getBy(frpChannel.getBy())
                            )
                        )
                        // channelActive and Read
                        servicePipeline.fireChannelActive()
                    } else {
                        log.warn("[ACCEPT] 接收到用户连接后，服务连接创建失败|dispatchId={}", dispatchId)
                        frpChannel.writeAndFlush(ServiceState.failure(dispatchId))
                            .addListeners(Listeners.read(frpChannel))
                    }
                })

                val b = Bootstrap()
                b.group(frpChannel.getBy().eventLoop())
                    .channel(frpChannel.getBy()::class.java)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                    .option(ChannelOption.AUTO_READ, false)
                    .handler(DirectServiceHandler(serviceChannelPromise))

                b.connect(config.localIp, config.localPort)
                    .addListener(ChannelFutureListener { cf ->
                        if (cf.isSuccess) {
                            frpChannel.writeAndFlush(ServiceState.success(dispatchId))
                                .addListener(Listeners.read(frpChannel))
                        } else {
                            log.warn("[ACCEPT] 接收到用户连接后，服务连接创建失败|name={}", config.name)
                            frpChannel.writeAndFlush(ServiceState.failure(dispatchId))
                                .addListeners(Listeners.read(frpChannel))
                        }
                    })
            }

            ConnState.READY -> {
                log.info(
                    "[READY] 接收到用户连接就绪状态，可以读取数据了|dispatchId={}",
                    connState.dispatchId
                )
                frpChannel.writeAndFlushEmpty()
                    .addListeners(
                        Listeners.read(
                            DispatchManager.getBy(frpChannel.getBy())
                                .getChannel(connState.dispatchId)!!
                        )
                    )
            }

            // broken connection：user -x-> frp-server:3306
            ConnState.BROKEN -> {
                log.warn("[BROKEN] 接收到用户断开连接|dispatchId={}", connState.dispatchId)
                frpChannel.writeAndFlushEmpty(
                    Listeners.read(frpChannel),
                    Listeners.releaseDispatchChannel(
                        DispatchManager.getBy(frpChannel.getBy()),
                        connState.dispatchId
                    )
                )
            }

            else -> {
                log.error("Unknown conn state: {}", userState)
                frpChannel.close()
            }
        }
    }
}