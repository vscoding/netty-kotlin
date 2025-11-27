package io.intellij.kotlin.netty.commons.utils

import io.intellij.kotlin.netty.commons.getLogger
import io.netty.channel.ChannelHandlerContext
import java.net.InetSocketAddress

/**
 * CtxUtils
 *
 * @author tech@intellij.io
 */
object CtxUtils {
    private val log = getLogger(CtxUtils::class.java)

    fun getRemoteAddress(ctx: ChannelHandlerContext): ConnInfo {
        try {
            return (ctx.channel().remoteAddress() as InetSocketAddress).let {
                ConnInfo.of(
                    it.address.hostAddress,
                    it.port
                )
            }
        } catch (e: Exception) {
            log.error(e.message)
            return ConnInfo.unknown()
        }
    }

    fun getLocalAddress(ctx: ChannelHandlerContext): ConnInfo {
        try {
            return (ctx.channel().localAddress() as InetSocketAddress).let {
                ConnInfo.of(
                    it.address.hostAddress,
                    it.port
                )
            }
        } catch (e: Exception) {
            log.error(e.message)
            return ConnInfo.unknown()
        }
    }

    fun getChannelId(ctx: ChannelHandlerContext): String {
        return ctx.channel().id().asLongText()
    }

}
