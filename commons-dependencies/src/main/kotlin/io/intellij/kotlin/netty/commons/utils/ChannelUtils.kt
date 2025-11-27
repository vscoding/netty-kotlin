package io.intellij.kotlin.netty.commons.utils

import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelPipeline

/**
 * ChannelUtils
 *
 * @author tech@intellij.io
 */
object ChannelUtils {

    fun closeOnFlush(ch: Channel?) {
        if (ch != null && ch.isActive) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE)
        }
    }

    fun close(ch: Channel?) {
        if (ch != null && ch.isActive) {
            ch.close()
        }
    }

    // 工具方法：打印 pipeline 链上的所有 handler 名称
    fun printPipeline(pipeline: ChannelPipeline) {
        val sb = StringBuilder("Pipeline chain: ")
        for (name in pipeline.names()) {
            sb.append(name).append(" -> ")
        }
        // 去掉最后一个箭头并输出
        if (sb.lastIndexOf(" -> ") == sb.length - 4) {
            sb.delete(sb.length - 4, sb.length)
        }
        println(sb)
    }
}
