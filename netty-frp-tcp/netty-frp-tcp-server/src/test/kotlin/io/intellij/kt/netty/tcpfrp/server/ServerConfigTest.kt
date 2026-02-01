package io.intellij.kt.netty.tcpfrp.server

import io.intellij.kt.netty.tcpfrp.config.ServerConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * ServerConfigTest
 *
 * @author tech@intellij.io
 */
class ServerConfigTest {

    @Test
    fun testGetServerConfig() {
        val config: ServerConfig =
            ServerConfig.init(ServerConfigTest::class.java.classLoader.getResourceAsStream("server-config.json"))
        System.err.println(config)
        Assertions.assertTrue(config.valid)
    }

}