package io.intellij.kt.netty.tcpfrp.protocol.channel

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.commons.utils.ChannelUtils
import io.netty.channel.Channel
import io.netty.channel.ChannelFutureListener
import io.netty.util.AttributeKey
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

/**
 * DispatchManager
 *
 * @author tech@intellij.io
 */
data class DispatchManager(
    private val idToChannelMap: MutableMap<String, Channel> = ConcurrentHashMap(),
    val enableDispatch: AtomicBoolean = AtomicBoolean(true)
) {

    companion object {
        private val log = getLogger(DispatchManager::class.java)
        val DISPATCH_MANAGER_KEY: AttributeKey<DispatchManager> = AttributeKey.valueOf("dispatch_manager")
    }

    fun putChannel(dispatchId: String, channel: Channel) {
        if (!enableDispatch.get()) {
            throw java.lang.RuntimeException("DispatchManager is disabled")
        }
        idToChannelMap[dispatchId] = channel
    }

    fun getChannelById(dispatchId: String?): Channel? {
        if (!enableDispatch.get()) {
            throw java.lang.RuntimeException("DispatchManager is disabled")
        }
        return idToChannelMap[dispatchId]
    }

    fun release(dispatchId: String) {
        if (!enableDispatch.get()) {
            throw java.lang.RuntimeException("DispatchManager is disabled")
        }
        log.warn("[Release] release channel|dispatchId={}", dispatchId)
        ChannelUtils.close(idToChannelMap.remove(dispatchId))
    }

    fun release(dispatchId: String, reason: String) {
        if (!enableDispatch.get()) {
            throw java.lang.RuntimeException("DispatchManager is disabled")
        }
        log.warn("[Release] release channel|dispatchId={}|reason={}", dispatchId, reason)
        ChannelUtils.close(idToChannelMap.remove(dispatchId))
    }

    fun releaseAll() {
        enableDispatch.set(false)
        log.warn("[Release] release all dispatch channels")
        idToChannelMap.values.forEach(ChannelUtils::close)
        idToChannelMap.clear()
    }

    fun dispatch(data: DispatchPacket, vararg listeners: ChannelFutureListener) {
        val channel = getChannelById(data.dispatchId)
        if (channel != null && channel.isActive) {
            channel.writeAndFlush(data.packet).addListeners(*listeners)
        } else {
            log.error("DispatchManager dispatch failed|dispatchId={}|channel={}", data.dispatchId, channel)
        }
    }

}

fun Channel.setDispatchManager() {
    attr(DispatchManager.DISPATCH_MANAGER_KEY).set(DispatchManager())
}

fun Channel.getDispatchManager(): DispatchManager {
    return attr(DispatchManager.DISPATCH_MANAGER_KEY).get()
        ?: throw RuntimeException("DispatchManager is not initialized")
}