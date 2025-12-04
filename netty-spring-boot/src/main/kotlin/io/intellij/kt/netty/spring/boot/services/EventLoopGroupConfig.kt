package io.intellij.kt.netty.spring.boot.services

import io.netty.channel.EventLoopGroup
import io.netty.channel.MultiThreadIoEventLoopGroup
import io.netty.channel.nio.NioIoHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * EventLoopGroupConfig
 *
 * @author tech@intellij.io
 */
@Configuration
class EventLoopGroupConfig {

    private val factory = NioIoHandler.newFactory()

    @Bean
    fun bossGroup(): EventLoopGroup {
        return MultiThreadIoEventLoopGroup(1, factory)
    }

    @Bean
    fun workerGroup(): EventLoopGroup {
        return MultiThreadIoEventLoopGroup(factory)
    }

}