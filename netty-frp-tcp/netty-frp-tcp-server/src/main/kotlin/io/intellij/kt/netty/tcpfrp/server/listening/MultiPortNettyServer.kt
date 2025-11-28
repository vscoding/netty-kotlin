package io.intellij.kt.netty.tcpfrp.server.listening

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.tcpfrp.commons.EventLoopGroups
import io.intellij.kt.netty.tcpfrp.protocol.channel.FrpChannel
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.util.AttributeKey
import java.util.concurrent.ConcurrentHashMap

/**
 * MultiPortNettyServer
 *
 * @author tech@intellij.io
 */
class MultiPortNettyServer(
    val ports: List<Int>,
    val frpChannel: FrpChannel
) {

    companion object {
        private val log = getLogger(MultiPortNettyServer::class.java)

        private var MULTI_PORT_NETTY_SERVER_KEY = AttributeKey.valueOf<MultiPortNettyServer>("multiPortNettyServer")

        fun buildIn(ch: Channel, server: MultiPortNettyServer) {
            ch.attr(MULTI_PORT_NETTY_SERVER_KEY).set(server)
        }

        fun stopIn(ch: Channel) {
            val server = ch.attr(MULTI_PORT_NETTY_SERVER_KEY).get()
            if (server != null) {
                server.stop()
                ch.attr(MULTI_PORT_NETTY_SERVER_KEY).set(null)
            }
        }

    }

    private val serverChannelMap = ConcurrentHashMap<Int, Channel>()

    fun start(): Boolean {
        val container: EventLoopGroups = EventLoopGroups.get()
        val bossGroup: EventLoopGroup = container.getBossGroup()
        val workerGroup: EventLoopGroup = container.getWorkerGroup()

        try {
            for (port in ports) {
                val b = ServerBootstrap()
                b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel::class.java)
                    .childOption<Boolean?>(ChannelOption.AUTO_READ, false)
                    .childHandler(object : ChannelInitializer<SocketChannel>() {
                        @Throws(Exception::class)
                        override fun initChannel(ch: SocketChannel) {
                            val pipeline = ch.pipeline()
                            pipeline.addLast(UserChannelHandler(port, frpChannel))
                        }
                    })

                // 绑定端口并启动服务器
                val channelFuture = b.bind(port).sync()

                serverChannelMap[port] = channelFuture.channel()

                log.info("frp-server listening on port {}", port)
            }

            return true
        } catch (e: Exception) {
            log.error("", e)
            this.stop()
            return false
        }
    }


    fun stop() {
        log.warn("Multi Port Server Stop Begin ...")
        serverChannelMap.forEach { (port: Int, channel: Channel) ->
            log.warn("stopped and release listening port {}", port)
            if (channel.isActive) {
                channel.close()
            }
        }
        log.warn("Multi Port Server Stop End   ...")
    }

}