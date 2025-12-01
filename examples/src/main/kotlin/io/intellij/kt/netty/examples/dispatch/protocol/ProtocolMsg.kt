package io.intellij.kt.netty.examples.dispatch.protocol

/**
 * ProtocolMsg
 *
 * @author tech@intellij.io
 */
data class ProtocolMsg(
    val type: ProtocolMsgType,
    val length: Int,
    val content: String
)

enum class ProtocolMsgType(
    val type: Int,
    val desc: String
) {
    HEARTBEAT(1, "心跳"),
    DATA(2, "数据");

    companion object {
        fun get(type: Int): ProtocolMsgType? {
            for (value in entries) {
                if (value.type == type) {
                    return value
                }
            }
            return null
        }
    }
}
