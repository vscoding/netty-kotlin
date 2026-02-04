package io.intellij.kt.netty.tcpfrp.protocol.channel

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel

/**
 * FrpChannelInitializer
 *
 * @author tech@intellij.io
 */
abstract class FrpChannelInitializer : ChannelInitializer<SocketChannel>() {

    @Throws(Exception::class)
    override fun initChannel(ch: SocketChannel) {
        ch.setFrpChannel()

        ch.config().isAutoRead = false // 默认不自动读取数据，等到准备好后再启用读取
        ch.pipeline().addLast(ByteCountingHandler())
        this.initChannel0(ch)
    }

    @Throws(Exception::class)
    protected abstract fun initChannel0(ch: SocketChannel)

}
