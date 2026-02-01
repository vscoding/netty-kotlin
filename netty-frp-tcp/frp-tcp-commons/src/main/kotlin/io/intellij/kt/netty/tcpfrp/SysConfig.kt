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
        const val DEF_SERVER_CONFIG: String = "server-config.json"
        const val DEF_CLIENT_CONFIG: String = "client-config.json"

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