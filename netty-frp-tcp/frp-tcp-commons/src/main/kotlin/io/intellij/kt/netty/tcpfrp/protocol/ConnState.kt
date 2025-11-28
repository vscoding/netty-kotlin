package io.intellij.kt.netty.tcpfrp.protocol

/**
 * ConnState
 *
 * @author tech@intellij.io
 */
enum class ConnState(val stateName: String, val desc: String) {
    /**
     * frp-server ---> frp-client
     *
     * [io.intellij.kt.netty.tcpfrp.protocol.server.UserState.Companion.accept]
     */
    ACCEPT("ACCEPT", "用户创建连接"),

    /**
     * frp-server ---> frp-client
     *
     * [io.intellij.kt.netty.tcpfrp.protocol.server.UserState.Companion.ready]
     */
    READY("READY", "用户连接准备就绪"),

    /**
     * frp-client ---> frp-server
     *
     * [io.intellij.kt.netty.tcpfrp.protocol.client.ServiceState.Companion.success]
     */
    SUCCESS("SUCCESS", "服务连接成功"),

    /**
     * frp-client ---> frp-server
     *
     * [io.intellij.kt.netty.tcpfrp.protocol.client.ServiceState.Companion.failure]
     */
    FAILURE("FAILURE", "服务连接失败"),

    /**
     * frp-client <==> frp-server
     *
     * [io.intellij.kt.netty.tcpfrp.protocol.server.UserState.Companion.broken]
     *
     * [io.intellij.kt.netty.tcpfrp.protocol.client.ServiceState.Companion.broken]
     */
    BROKEN("BROKEN", "连接断开");

    companion object {
        fun getByName(stateName: String): ConnState? {
            for (connState in entries) {
                if (connState.stateName == stateName) {
                    return connState
                }
            }
            return null
        }
    }
}
