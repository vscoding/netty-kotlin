package io.intellij.kt.netty.spring.boot.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * NettyServerApi
 *
 * @author tech@intellij.io
 */
@RestController
@RequestMapping("/netty")
class NettyServerApi {

    @PostMapping("/server/start")
    fun start() {
    }

    @PostMapping("/server/status")
    fun status() {
    }

    @PostMapping("/server/stop")
    fun stop() {
    }

}