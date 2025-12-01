package io.intellij.kt.netty.spring.boot.services

import io.intellij.kt.netty.spring.boot.entities.NettyServerConf
import io.intellij.kt.netty.spring.boot.entities.ServerRunRes

/**
 * NettyServerService
 *
 * @author tech@intellij.io
 */
interface NettyServerService {

    /**
     * Starts a Netty server with the specified configuration.
     *
     * @param conf the configuration for the Netty server, including the port to bind
     * @return the result of starting the server, containing the success status and any message
     */
    fun start(conf: NettyServerConf): ServerRunRes

    /**
     * Checks whether the server is currently running on the given port.
     *
     * @param port the port number to check if the server is running on
     * @return true if the server is running on the specified port, false otherwise
     */
    fun isRunning(port: Int): Boolean

    /**
     * Stops the Netty server running on the specified port.
     *
     * @param port the port number on which the server is running and needs to be stopped
     */
    fun stop(port: Int)

    /**
     * Stops all running Netty servers.
     */
    fun stopAll()


}