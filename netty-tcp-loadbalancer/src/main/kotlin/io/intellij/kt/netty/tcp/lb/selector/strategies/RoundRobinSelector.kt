package io.intellij.kt.netty.tcp.lb.selector.strategies

import io.intellij.kt.netty.tcp.lb.config.Backend
import io.intellij.kt.netty.tcp.lb.selector.AbstractBackendSelector
import java.util.concurrent.atomic.AtomicInteger

/**
 * RoundRobinSelector
 *
 * @author tech@intellij.io
 */
class RoundRobinSelector(backends: Map<String, Backend>) : AbstractBackendSelector(backends) {

    companion object {
        private val ROUND_ROBIN_INDEX = AtomicInteger(-1)
    }

    private val backendIndex = HashMap<String, Int>()
    private val indexBackend = HashMap<Int, Backend>()

    init {
        val names = this.backends.keys.sorted().toList()
        for (i in names.indices) {
            backendIndex[names[i]] = i
            indexBackend[i] = backends[names[i]]!!
        }
    }

    override fun afterActive(target: Backend) {
        val name = target.name
        // set the index of the backend, for the next round
        ROUND_ROBIN_INDEX.set(backendIndex[name]!!)
    }

    override fun doSelect(): Backend? {
        val availableList: List<String> = availableList()
        if (availableList.isEmpty()) {
            return null
        }
        val i = ROUND_ROBIN_INDEX.get()
        val size = availableList.size
        if (i < 0 || size == 1) {
            return indexBackend[0]
        }

        val index = (i + 1) % size
        return indexBackend[index]
    }

}
