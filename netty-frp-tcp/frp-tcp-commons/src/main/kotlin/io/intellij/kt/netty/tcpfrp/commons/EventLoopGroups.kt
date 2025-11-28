package io.intellij.kt.netty.tcpfrp.commons

import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup

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

    init {
        this.bossGroup = null
        this.workerGroup = null
    }

    fun getBossGroup(): EventLoopGroup {
        if (this.bossGroup == null) {
            this.bossGroup = NioEventLoopGroup(1)
        }
        return this.bossGroup!!
    }

    fun getWorkerGroup(): EventLoopGroup {
        if (this.workerGroup == null) {
            this.workerGroup = NioEventLoopGroup()
        }
        return this.workerGroup!!
    }

    fun getWorkerGroup(nThreads: Int): EventLoopGroup {
        if (this.workerGroup == null) {
            this.workerGroup = NioEventLoopGroup(nThreads)
        }
        return this.workerGroup!!
    }

}
