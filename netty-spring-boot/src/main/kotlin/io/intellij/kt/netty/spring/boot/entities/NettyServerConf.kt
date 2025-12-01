package io.intellij.kt.netty.spring.boot.entities

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

/**
 * NettyServerConf
 *
 * @author tech@intellij.io
 */
data class NettyServerConf(
    @field:NotNull
    @field:Min(1024)
    @field:Max(65535)
    val port: Int,

    var handlerKey: String? = null
)
