package io.intellij.kt.netty.server.connector

import io.intellij.kt.netty.commons.getLogger
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * Connector
 *
 * @author tech@intellij.io
 */
class Connector(
    private val serverAddr: SocketAddress,
    private val ses: ScheduledExecutorService,
    bootstrapInit: (Bootstrap) -> Unit
) {
    private val log = getLogger(Connector::class.java)
    private val bootstrap = Bootstrap()

    @Volatile
    private var _channel: Channel? = null

    val channel: Channel?
        get() = _channel

    init {
        // 对应 bootstrapInit.accept(bootstrap)
        bootstrapInit(bootstrap)
    }

    constructor(host: String, port: Int, bootstrapInit: (Bootstrap) -> Unit) :
            this(
                serverAddr = InetSocketAddress(host, port),
                ses = Executors.newSingleThreadScheduledExecutor(),
                bootstrapInit = bootstrapInit
            )

    constructor(
        host: String,
        port: Int,
        ses: ScheduledExecutorService,
        bootstrapInit: (Bootstrap) -> Unit
    ) : this(
        serverAddr = InetSocketAddress(host, port),
        ses = ses,
        bootstrapInit = bootstrapInit
    )

    fun connect() {
        this.doConnect()
    }

    fun connect(msDelay: Long) {
        this.connect(msDelay, TimeUnit.MILLISECONDS)
    }

    fun connect(delay: Long, unit: TimeUnit) {
        ses.schedule({ this.doConnect() }, delay, unit)
    }

    private fun doConnect() {
        try {
            val channelFuture = bootstrap.connect(serverAddr)

            channelFuture.addListener(object : ChannelFutureListener {
                @Throws(Exception::class)
                override fun operationComplete(future: ChannelFuture) {
                    if (future.isSuccess) {
                        _channel = future.channel()
                        // 服务端断开连接
                        addCloseDetectListener(_channel!!)
                        log.info("connection established")
                    } else {
                        _channel = null
                        log.error("connection lost in bootstrap.connect")
                        // bootstrap.connect(serverAddr).addListener(this);
                        connect(1000)
                    }
                }

                fun addCloseDetectListener(channel: Channel) {
                    // if the channel connection is lost, the ChannelFutureListener.operationComplete() will be called
                    channel.closeFuture().addListener(object : ChannelFutureListener {
                        @Throws(Exception::class)
                        override fun operationComplete(future: ChannelFuture) {
                            _channel = null
                            log.error("connection lost in detect listener")
                            connect(1000)
                        }
                    })
                }
            })
        } catch (e: Exception) {
            log.error("do Connect Failed", e)
        }
    }

}