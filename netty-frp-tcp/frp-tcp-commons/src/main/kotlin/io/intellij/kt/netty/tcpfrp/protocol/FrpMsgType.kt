package io.intellij.kt.netty.tcpfrp.protocol

/**
 * FrpMsgType
 *
 * @author tech@intellij.io
 */
enum class FrpMsgType(val type: Int, val desc: String) {
    /**
     * 客户端：认证请求
     */
    AUTH_REQUEST(0, "auth request"),

    /**
     * 服务端：认证回复
     */
    AUTH_RESPONSE(1, "auth response"),

    /**
     * 客户端 -> 服务端: 发送监听配置给服务端
     */
    LISTENING_REQUEST(2, "listening request"),

    /**
     * 服务端 -> 客户端: 告知客户端监听的配置列表的回复
     */
    LISTENING_RESPONSE(3, "listening response"),

    /**
     * 服务端 -> 客户端：
     *
     *
     * 用户连接请求 (连接事件 用户建立连接 e.g. user ---> frp-server:3306)
     *
     *
     * 用户连接断开 (连接事件 用户建立连接 e.g. user -×-> frp-server:3306)
     */
    USER_STATE(4, "user conn state"),

    /**
     * 客户端 -> 服务端
     *
     *
     * frp-client连接到真实服务成功：（e.g. frp-client ---> mysql:3306）
     * frp-client连接到真实服务失败：（e.g. frp-client -x-> mysql:3306）
     */
    SERVICE_STATE(5, "service conn state"),

    /**
     * 客户端&服务端: 数据包，本质上结构一样 (type|dispatchId|data)
     */
    DATA_PACKET(6, "data packet"),

    PING(7, "ping"),

    PONG(8, "pong");

    companion object {
        fun getByType(type: Int): FrpMsgType? {
            for (msgType in entries) {
                if (msgType.type == type) {
                    return msgType
                }
            }
            return null
        }
    }
}
