package io.intellij.kt.netty.tcp.lb.config

/**
 * LbConfig
 *
 * @author tech@intellij.io
 */
data class LbConfig(val port: Int, val strategy: LbStrategy, val backends: Map<String, Backend>)

enum class LbStrategy {
    ROUND_ROBIN,
    RANDOM,
    LEAST_CONN,
    HASH;

    companion object {
        fun fromString(strategy: String): LbStrategy {
            return when (strategy) {
                "round_robin" -> ROUND_ROBIN
                "least_conn" -> LEAST_CONN
                "hash" -> HASH
                else -> RANDOM
            }
        }
    }
}
