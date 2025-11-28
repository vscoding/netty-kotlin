package io.intellij.kt.netty.tcp.lb.selector

import io.intellij.kt.netty.tcp.lb.config.Backend
import java.util.concurrent.ConcurrentHashMap

/**
 * AbstractBackendSelector
 *
 * @author tech@intellij.io
 */
abstract class AbstractBackendSelector protected constructor(val backends: Map<String, Backend>) :
    BackendSelector {

    // available status map
    private val availableStatus: MutableMap<String, Boolean> = ConcurrentHashMap<String, Boolean>()

    // connection count map
    private val connectionCount: MutableMap<String, Int> = ConcurrentHashMap<String, Int>()

    override fun select(): Backend? {
        return this.doSelect()
    }

    override fun nextIfConnectFailed(selected: Backend): Backend? {
        availableStatus[selected.name] = false
        return this.doSelect()
    }

    // hook method for subclasses
    protected abstract fun doSelect(): Backend?

    override fun onChannelActive(target: Backend) {
        val name = target.name
        availableStatus[name] = true
        connectionCount[name] = connectionCount.getOrDefault(name, 0) + 1
        this.afterActive(target)
    }

    override fun onChannelInactive(target: Backend) {
        val name = target.name
        connectionCount[name] = connectionCount.getOrDefault(name, 0) - 1
        this.afterInactive(target)
    }

    protected open fun afterActive(target: Backend) {
        // hook method for subclasses
    }

    protected open fun afterInactive(target: Backend) {
        // hook method for subclasses
    }

    override fun reset() {
        availableStatus.clear()
        connectionCount.clear()
    }

    protected fun accessStatusMap(): Map<String, Boolean> {
        return availableStatus
    }

    protected fun connectionCountMap(): Map<String, Int> {
        return connectionCount
    }

    protected fun availableList(): List<String> {
        val map = accessStatusMap()
        return this.backends.keys
            .filter { name -> map[name] != false }   // null 或 true -> 保留；false -> 过滤
            .sorted()
            .toList()
    }

}
