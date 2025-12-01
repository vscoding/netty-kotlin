package io.intellij.kt.netty.spring.boot.services

import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * EventLoopGroupConfig
 *
 * @author tech@intellij.io
 */
@Configuration
class EventLoopGroupConfig {

    @Bean
    fun bossGroup(): EventLoopGroup {
        return NioEventLoopGroup(1)
    }

    @Bean
    fun workerGroup(): EventLoopGroup {
        return NioEventLoopGroup()
    }

}