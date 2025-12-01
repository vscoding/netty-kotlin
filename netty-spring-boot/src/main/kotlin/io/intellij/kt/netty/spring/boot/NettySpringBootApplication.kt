package io.intellij.kt.netty.spring.boot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * NettySpringBootApplication
 *
 * @author tech@intellij.io
 */
@SpringBootApplication
class NettySpringBootApplication

fun main(args: Array<String>) {
    runApplication<NettySpringBootApplication>(*args)
}
