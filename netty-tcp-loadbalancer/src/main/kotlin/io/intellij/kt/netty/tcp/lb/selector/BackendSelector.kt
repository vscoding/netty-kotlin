package io.intellij.kt.netty.tcp.lb.selector

import io.intellij.kt.netty.tcp.lb.config.Backend
import io.intellij.kt.netty.tcp.lb.config.LbStrategy
import io.intellij.kt.netty.tcp.lb.selector.strategies.LeastConnSelector
import io.intellij.kt.netty.tcp.lb.selector.strategies.RandomSelector
import io.intellij.kt.netty.tcp.lb.selector.strategies.RoundRobinSelector

/**
 * BackendSelector
 *
 * @author tech@intellij.io
 */
interface BackendSelector {

    fun select(): Backend?

    fun nextIfConnectFailed(selected: Backend): Backend?

    fun onChannelActive(target: Backend)

    fun onChannelInactive(target: Backend)

    fun reset()

    companion object {
        fun get(strategy: LbStrategy, backends: Map<String, Backend>): BackendSelector {
            return when (strategy) {
                LbStrategy.RANDOM -> RandomSelector(backends)
                LbStrategy.ROUND_ROBIN -> RoundRobinSelector(backends)
                LbStrategy.LEAST_CONN -> LeastConnSelector(backends)
                else -> throw IllegalStateException("Unexpected value: ${strategy.name}")
            }
        }
    }

}
