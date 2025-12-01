package io.intellij.kt.netty.spring.boot.services

import io.intellij.kt.netty.commons.handlers.ActiveHandler
import io.intellij.kt.netty.commons.handlers.EchoHandler
import io.intellij.kt.netty.commons.handlers.LogHandler
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * InitializerConfig
 *
 * @author tech@intellij.io
 */
@Configuration
class InitializerConfig {

    @Bean(name = ["log"])
    fun logChannelHandler(): ChannelHandler {
        return object : ChannelInitializer<SocketChannel>() {
            @Throws(Exception::class)
            override fun initChannel(ch: SocketChannel) {
                ch.pipeline()
                    .addLast(ActiveHandler())
                    .addLast(LogHandler())
            }
        }
    }

    @Bean(name = ["echo"])
    fun echoChannelHandler(): ChannelHandler {
        return object : ChannelInitializer<SocketChannel>() {
            @Throws(Exception::class)
            override fun initChannel(ch: SocketChannel) {
                ch.pipeline()
                    .addLast(ActiveHandler())
                    .addLast(EchoHandler())
            }
        }
    }
}