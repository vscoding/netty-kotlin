package io.intellij.kt.netty.tcpfrp.server

import io.intellij.kt.netty.tcpfrp.SysConfig.Companion.CONFIG_PATH_PROPERTY
import io.intellij.kt.netty.tcpfrp.config.ServerConfig.Companion.loadConfig

/**
 * FrpServerMain
 *
 * @author tech@intellij.io
 */
object FrpServerMain {

    @JvmStatic
    fun main(args: Array<String>) {
        loadConfig(System.getProperty(CONFIG_PATH_PROPERTY, "frps.json"))
            .then(FrpServer::start)
    }

}