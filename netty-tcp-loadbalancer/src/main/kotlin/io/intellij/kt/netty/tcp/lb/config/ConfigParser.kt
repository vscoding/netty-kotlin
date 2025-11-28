package io.intellij.kt.netty.tcp.lb.config

import com.alibaba.fastjson2.JSONObject
import io.intellij.kt.netty.commons.getLogger
import org.apache.commons.io.IOUtils
import java.nio.charset.StandardCharsets

/**
 * ConfigParser
 *
 * @author tech@intellij.io
 */
object ConfigParser {
    private val log = getLogger(ConfigParser::class.java)

    fun loadConfig(configPath: String): LbConfig? {
        try {
            ConfigParser::class.java.classLoader.getResourceAsStream(configPath).use { input ->
                if (input == null) {
                    log.error("config file not found: $configPath")
                    return null
                }
                val json = IOUtils.readLines(input, StandardCharsets.UTF_8).joinToString("\n")
                val obj = JSONObject.parseObject(json)

                val port = obj.getJSONObject("local").getIntValue("port")
                val lbStrategy = obj.getString("lbStrategy")
                val backendsList = obj.getJSONArray("backends").toJavaList(Backend::class.java)
                val backends = backendsList.associateBy { it.name }

                log.warn("TODO: validate config")
                return LbConfig(
                    port = port,
                    strategy = LbStrategy.fromString(lbStrategy),
                    backends = backends
                )
            }
        } catch (e: Exception) {
            log.error("load config error", e)
            return null
        }
    }
}