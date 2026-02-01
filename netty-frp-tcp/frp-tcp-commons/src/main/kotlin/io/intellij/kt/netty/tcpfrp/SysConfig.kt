package io.intellij.kt.netty.tcpfrp

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.tcpfrp.protocol.TlsContexts


/**
 * SysConfig
 *
 * @author tech@intellij.io
 */
class SysConfig private constructor() {
    companion object {
        private val log = getLogger(SysConfig::class.java)
        const val CONFIG_PATH_PROPERTY: String = "configPath"

        private val instance: SysConfig = SysConfig()
        fun get(): SysConfig {
            return instance
        }
    }

    // 初始化默认为false
    var enableSsl: Boolean = false

    init {
        TlsContexts.init(this)
    }

    fun logDetails() {
        log.info("======== SysConfig Details ========")
        log.info("ENABLE_SSL={}", this.enableSsl)
        log.info("===================================")
    }

}