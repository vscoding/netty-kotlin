package io.intellij.kt.netty.spring.boot.controller

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.spring.boot.entities.NettyServerConf
import io.intellij.kt.netty.spring.boot.entities.ServerRunRes
import io.intellij.kt.netty.spring.boot.services.NettyServerService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * NettyServerApi
 *
 * @author tech@intellij.io
 */
@RestController
@RequestMapping("/netty")
class NettyServerApi(
    private val nettyServerService: NettyServerService
) {

    companion object {
        private val log = getLogger(NettyServerApi::class.java)
    }

    @PostMapping("/server/start")
    fun startServer(@RequestBody @Validated conf: NettyServerConf): ServerRunRes {
        log.info("Starting Netty Server")
        return nettyServerService.start(conf)
    }

    @PostMapping("/server/status")
    fun isServerRunning(@RequestBody @Validated conf: NettyServerConf): Map<String, Any> {
        log.info("Checking if Netty Server is running")
        if (nettyServerService.isRunning(conf.port)) {
            log.info("Netty Server is running")
            return mapOf<String, Any>(
                "code" to 200,
                "port" to conf.port,
                "msg" to "Netty Server is running"
            )
        } else {
            log.info("Netty Server is not running")
            return mapOf<String, Any>(
                "code" to 500,
                "port" to conf.port,
                "msg" to "Netty Server is not running"
            )
        }
    }

    @PostMapping("/server/stop")
    fun stopServer(@RequestBody @Validated conf: NettyServerConf) {
        log.warn("Stopping Netty Server")
        nettyServerService.stop(conf.port)
    }
}
