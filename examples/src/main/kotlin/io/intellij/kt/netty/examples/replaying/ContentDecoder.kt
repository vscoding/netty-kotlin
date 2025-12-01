package io.intellij.kt.netty.examples.replaying

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ReplayingDecoder

/**
 * ContentDecoder
 *
 * @author tech@intellij.io
 */
class ContentDecoder  // 构造函数指定初始状态为 READ_LENGTH
    : ReplayingDecoder<ContentDecoder.State>(State.READ_LENGTH) {

    // 定义解码器状态：读取消息长度和消息内容
    enum class State {
        READ_LENGTH,
        READ_CONTENT
    }

    private var length = 0 // 保存读取到的消息长度

    @Throws(Exception::class)
    override fun decode(ctx: ChannelHandlerContext?, `in`: ByteBuf, out: MutableList<Any?>) {
        when (state()) {
            State.READ_LENGTH -> {
                // 假设前4个字节表示消息长度
                length = `in`.readInt()
                // 设置检查点，同时切换到读取消息内容的状态
                checkpoint(State.READ_CONTENT)
                // 读取指定长度的消息内容
                val content = `in`.readBytes(length)
                // 将解码后的数据放入输出列表
                out.add(content)
                // 解码成功后，重置状态为 READ_LENGTH，并设置新的检查点
                checkpoint(State.READ_LENGTH)
            }

            State.READ_CONTENT -> {
                val content = `in`.readBytes(length)
                out.add(content)
                checkpoint(State.READ_LENGTH)
            }

            else -> throw IllegalStateException("无效的状态: " + state())
        }
    }
}
