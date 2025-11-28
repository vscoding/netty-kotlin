package io.intellij.kt.netty.tcp.lb.handlers

import io.intellij.kt.netty.tcp.lb.config.Backend
import io.intellij.kt.netty.tcp.lb.config.LbStrategy
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel

/**
 * LoadBalancerInitializer
 *
 * @author tech@intellij.io
 */
class LoadBalancerInitializer(
    val strategy: LbStrategy,
    val backends: Map<String, Backend>
) : ChannelInitializer<SocketChannel>() {

    @Throws(Exception::class)
    override fun initChannel(ch: SocketChannel) {
        ch.pipeline().addLast(FrontendInboundHandler(strategy, backends))
    }

}
