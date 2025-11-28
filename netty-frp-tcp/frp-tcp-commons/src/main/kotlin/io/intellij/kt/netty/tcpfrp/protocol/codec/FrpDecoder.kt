package io.intellij.kt.netty.tcpfrp.protocol.codec

import com.alibaba.fastjson2.JSON
import io.intellij.kt.netty.tcpfrp.protocol.FrpBasicMsg
import io.intellij.kt.netty.tcpfrp.protocol.FrpBasicMsg.State.READ_BASIC_MSG
import io.intellij.kt.netty.tcpfrp.protocol.FrpBasicMsg.State.READ_DISPATCH_PACKET
import io.intellij.kt.netty.tcpfrp.protocol.FrpBasicMsg.State.READ_LENGTH
import io.intellij.kt.netty.tcpfrp.protocol.FrpBasicMsg.State.READ_TYPE
import io.intellij.kt.netty.tcpfrp.protocol.FrpMsgType
import io.intellij.kt.netty.tcpfrp.protocol.channel.DispatchIdUtils
import io.intellij.kt.netty.tcpfrp.protocol.channel.DispatchPacket
import io.intellij.kt.netty.tcpfrp.protocol.client.AuthRequest
import io.intellij.kt.netty.tcpfrp.protocol.client.ListeningRequest
import io.intellij.kt.netty.tcpfrp.protocol.client.ServiceState
import io.intellij.kt.netty.tcpfrp.protocol.heartbeat.Ping
import io.intellij.kt.netty.tcpfrp.protocol.heartbeat.Pong
import io.intellij.kt.netty.tcpfrp.protocol.server.AuthResponse
import io.intellij.kt.netty.tcpfrp.protocol.server.ListeningResponse
import io.intellij.kt.netty.tcpfrp.protocol.server.UserState
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ReplayingDecoder

/**
 * FrpDecoder
 */
class FrpDecoder(
    private val mode: Mode
) : ReplayingDecoder<FrpBasicMsg.State>(READ_TYPE) {

    // 指定客户端或者服务端
    enum class Mode { SERVER, CLIENT }

    private var type: FrpMsgType? = null
    private var length: Int = 0

    override fun decode(ctx: ChannelHandlerContext, `in`: ByteBuf, out: MutableList<Any>) {
        // 用循环模拟 Java switch 的“可能贯穿”行为，每个分支完成后根据需要 checkpoint 并 return/continue
        while (true) {
            when (state()) {
                READ_TYPE -> {
                    val t = FrpMsgType.getByType(`in`.readByte().toInt())
                        ?: throw IllegalStateException("无效的消息类型")
                    type = t
                    if (t == FrpMsgType.DATA_PACKET) {
                        checkpoint(READ_DISPATCH_PACKET)
                        continue
                    }
                    checkpoint(READ_LENGTH)
                    // 不 return，落入下一状态（与 Java 的 fall-through 等价）
                }

                READ_LENGTH -> {
                    length = `in`.readInt()
                    if (length <= 0) throw IllegalStateException("无效的消息长度")
                    checkpoint(READ_BASIC_MSG)
                    // 继续落入下一状态
                }

                READ_BASIC_MSG -> {
                    val content = ByteArray(length)
                    `in`.readBytes(content)
                    val json = String(content)

                    when (mode) {
                        Mode.SERVER -> {
                            when (type) {
                                FrpMsgType.AUTH_REQUEST ->
                                    out.add(jsonToObj(json, AuthRequest::class.java, "auth request parse error"))

                                FrpMsgType.LISTENING_REQUEST ->
                                    out.add(
                                        jsonToObj(
                                            json,
                                            ListeningRequest::class.java,
                                            "listening request parse error"
                                        )
                                    )

                                FrpMsgType.SERVICE_STATE ->
                                    out.add(jsonToObj(json, ServiceState::class.java, "service state parse error"))

                                FrpMsgType.PING ->
                                    out.add(jsonToObj(json, Ping::class.java, "ping parse error"))

                                else -> throw IllegalStateException("无效的消息类型: $type")
                            }
                        }

                        Mode.CLIENT -> {
                            when (type) {
                                FrpMsgType.AUTH_RESPONSE ->
                                    out.add(jsonToObj(json, AuthResponse::class.java, "auth response parse error"))

                                FrpMsgType.LISTENING_RESPONSE ->
                                    out.add(
                                        jsonToObj(
                                            json,
                                            ListeningResponse::class.java,
                                            "listening response parse error"
                                        )
                                    )

                                FrpMsgType.USER_STATE ->
                                    out.add(jsonToObj(json, UserState::class.java, "user state parse error"))

                                FrpMsgType.PONG ->
                                    out.add(jsonToObj(json, Pong::class.java, "pong parse error"))

                                else -> throw IllegalStateException("无效的消息类型: $type")
                            }
                        }
                    }

                    checkpoint(READ_TYPE)
                    return
                }

                READ_DISPATCH_PACKET -> {
                    val dispatchIdBytes = ByteArray(DispatchIdUtils.ID_LENGTH)
                    `in`.readBytes(dispatchIdBytes)
                    val dispatchId = String(dispatchIdBytes)
                    val packetLen = `in`.readInt()
                    if (packetLen <= 0) throw IllegalStateException("无效的DispatchPacket消息长度")
                    out.add(DispatchPacket.createAndRetain(dispatchId, `in`.readSlice(packetLen)))
                    checkpoint(READ_TYPE)
                    return
                }
            }
        }
    }

    private fun <T> jsonToObj(json: String, clazz: Class<T>, err: String): T {
        return JSON.parseObject(json, clazz) ?: throw IllegalStateException(err)
    }

}