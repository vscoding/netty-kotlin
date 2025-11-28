package io.intellij.kt.netty.tcpfrp.client

import io.intellij.kt.netty.tcpfrp.SysConfig.Companion.CONFIG_PATH_PROPERTY
import io.intellij.kt.netty.tcpfrp.SysConfig.Companion.DEF_CLIENT_CONFIG
import io.intellij.kt.netty.tcpfrp.config.ClientConfig.Companion.loadConfig

/**
 * FrpClientMain
 *
 * @author tech@intellij.io
 */
object FrpClientMain {

    @JvmStatic
    fun main(args: Array<String>) {
        loadConfig(System.getProperty(CONFIG_PATH_PROPERTY, DEF_CLIENT_CONFIG))
            .then(FrpClient::startReconnect)
    }

}