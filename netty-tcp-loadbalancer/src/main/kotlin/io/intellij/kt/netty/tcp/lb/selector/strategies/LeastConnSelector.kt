package io.intellij.kt.netty.tcp.lb.selector.strategies

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.tcp.lb.config.Backend
import io.intellij.kt.netty.tcp.lb.selector.AbstractBackendSelector
import java.util.Objects

/**
 * LeastConnSelector
 *
 * @author tech@intellij.io
 */
class LeastConnSelector(backends: Map<String, Backend>) : AbstractBackendSelector(backends) {

    companion object {
        private val log = getLogger(LeastConnSelector::class.java)
    }

    override fun doSelect(): Backend? {
        // 可以用堆优化 选择连接数最少的后端
        val availableList: List<String> = this.availableList()
        if (availableList.isEmpty()) {
            return null
        }

        if (availableList.size == 1) {
            return backends[availableList[0]]
        }

        // 以下获取最小连接数的后端

        var tmpName = availableList[0]
        var tmpCount: Int? = this.connectionCountMap()[tmpName]

        if (tmpCount == null) {
            // 如果当前连接没有连接计数，一定是最小的，直接返回
            return backends[tmpName]
        } else {
            for (i in 1..availableList.size) {
                val name = availableList[i]
                val count: Int? = this.connectionCountMap()[name]
                if (Objects.isNull(count)) {
                    // 如果当前连接没有连接计数，一定是最小的，直接返回
                    return backends[name]
                } else if (count!! < tmpCount!!) {
                    // 找到更小的连接数
                    tmpName = name
                    tmpCount = count
                }
            }
            return backends[tmpName]
        }
    }

    override fun afterActive(target: Backend) {
        connectionCountMap().forEach { (k, v) ->
            log.info("After Active|name = {}, count = {}", k, v)
        }
    }

    override fun afterInactive(target: Backend) {
        connectionCountMap().forEach { (k, v) ->
            log.info("After Inactive|name = {}, count = {}", k, v)
        }
    }

}
