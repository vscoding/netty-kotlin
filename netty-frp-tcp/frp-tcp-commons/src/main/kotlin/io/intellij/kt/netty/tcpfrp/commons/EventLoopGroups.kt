package io.intellij.kt.netty.tcpfrp.commons

import io.netty.channel.EventLoopGroup
import io.netty.channel.MultiThreadIoEventLoopGroup
import io.netty.channel.nio.NioIoHandler

/**
 * EventLoopGroups
 *
 * @author tech@intellij.io
 */
class EventLoopGroups private constructor() {

    companion object {
        private val instance = EventLoopGroups()
        fun get(): EventLoopGroups {
            return instance
        }
    }

    private var bossGroup: EventLoopGroup?
    private var workerGroup: EventLoopGroup?

    private val factory = NioIoHandler.newFactory()

    init {
        this.bossGroup = null
        this.workerGroup = null
    }

    fun getBossGroup(): EventLoopGroup {
        if (this.bossGroup == null) {
            this.bossGroup = MultiThreadIoEventLoopGroup(1, factory)
        }
        return this.bossGroup!!
    }

    fun getWorkerGroup(): EventLoopGroup {
        if (this.workerGroup == null) {
            this.workerGroup = MultiThreadIoEventLoopGroup(factory)
        }
        return this.workerGroup!!
    }

    fun getWorkerGroup(nThreads: Int): EventLoopGroup {
        if (this.workerGroup == null) {
            this.workerGroup = MultiThreadIoEventLoopGroup(nThreads, factory)
        }
        return this.workerGroup!!
    }

}
