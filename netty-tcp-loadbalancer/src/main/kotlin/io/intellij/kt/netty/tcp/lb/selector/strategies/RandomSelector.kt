package io.intellij.kt.netty.tcp.lb.selector.strategies

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.tcp.lb.config.Backend
import io.intellij.kt.netty.tcp.lb.selector.AbstractBackendSelector

/**
 * RandomSelector
 *
 * @author tech@intellij.io
 */
class RandomSelector(backends: Map<String, Backend>) : AbstractBackendSelector(backends) {

    companion object {
        private val log = getLogger(RandomSelector::class.java)
    }

    override fun doSelect(): Backend? {
        val accessStatusMap: Map<String, Boolean> = accessStatusMap()
        val failedMap = accessStatusMap.filterValues { !it }
        if (failedMap.size == backends.size) {
            // all backends are unavailable
            return null
        }
        val accessBackends = backends.filter { it.key !in failedMap }.values.toList()
        return randomIn(accessBackends)
    }

    private fun randomIn(accessBackends: List<Backend>): Backend {
        val size = accessBackends.size
        val randomIndex = (Math.random() * size).toInt()
        return accessBackends[randomIndex]
    }

    override fun afterActive(target: Backend) {
        log.info("========> active statistic start ========== | target: {}", target.name)
        connectionCountMap().forEach { (k, v) ->
            log.info("Random Selector Active|name = {}, count = {}", k, v)
        }
        log.info("========> active statistic end   ==========\n")
    }

    override fun afterInactive(target: Backend) {
        log.info("========> inactive start ========== | target: {}", target.name)
        connectionCountMap().forEach { (k, v) ->
            log.info("Random Selector Inactive|name = {}, count = {}", k, v)
        }
        log.info("========> inactive end   ==========\n")
    }

}
