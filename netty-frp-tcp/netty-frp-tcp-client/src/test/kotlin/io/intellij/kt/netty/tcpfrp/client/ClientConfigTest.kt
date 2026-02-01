package io.intellij.kt.netty.tcpfrp.client

import io.intellij.kt.netty.tcpfrp.config.ClientConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * ClientConfigTest
 *
 * @author tech@intellij.io
 */
class ClientConfigTest {

    @Test
    fun testGetClientConfig() {
        val clientConfig =
            ClientConfig.init(ClientConfigTest::class.java.classLoader.getResourceAsStream("client-config.json"))
        System.err.println(clientConfig)
        Assertions.assertTrue(clientConfig.valid)
    }

}