package io.intellij.kt.netty.server.connector

import io.intellij.kt.netty.commons.getLogger
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.util.concurrent.TimeUnit

/**
 * Connector
 *
 * @author tech@intellij.io
 */
class Connector private constructor(
    private val serverAddr: SocketAddress,
    private val worker: EventLoopGroup,
) {
    private val log = getLogger(Connector::class.java)
    private val bootstrap = Bootstrap()

    @Volatile
    private var _ch: Channel? = null

    init {
        // init 是主构造函数的一部分
        bootstrap.group(this.worker)
            .channel(NioSocketChannel::class.java)
            .option(ChannelOption.SO_KEEPALIVE, true)
    }

    constructor(
        host: String, port: Int,
        worker: EventLoopGroup,
        initializer: ChannelInitializer<Channel>,
    ) : this(InetSocketAddress(host, port), worker) {
        bootstrap.handler(initializer)
    }

    fun connect(msDelay: Long = 1000L) {
        this.connect(msDelay, TimeUnit.MILLISECONDS)
    }

    fun connect(delay: Long, unit: TimeUnit) {
        this.worker.schedule({ this.doConnect() }, delay, unit)
    }

    private fun doConnect() {
        try {
            val channelFuture = bootstrap.connect(serverAddr)

            channelFuture.addListener(object : ChannelFutureListener {

                @Throws(Exception::class)
                override fun operationComplete(future: ChannelFuture) {
                    if (future.isSuccess) {
                        _ch = future.channel()
                        // 服务端断开连接
                        // addCloseFutureListener(_ch!!)
                        _ch!!.addCloseFutureListener()
                        log.info("connection established")
                    } else {
                        _ch = null
                        log.error("connection lost in bootstrap.connect")
                        connect(1000L)
                    }
                }
            })
        } catch (e: Exception) {
            log.error("do Connect Failed", e)
        }
    }

    private fun Channel.addCloseFutureListener() {
        // if the channel connection is lost, the ChannelFutureListener.operationComplete() will be called
        this.closeFuture().addListener(object : ChannelFutureListener {
            @Throws(Exception::class)
            override fun operationComplete(future: ChannelFuture) {
                _ch = null
                log.error("connection lost in detect listener")
                connect(1000)
            }
        })
    }

    fun writeAndFlush(msg: Any) = _ch?.run {
        if (this.isActive) {
            log.info("write msg|{}", msg)
            this.writeAndFlush(msg)
        }
    }

}