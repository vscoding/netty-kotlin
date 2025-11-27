package io.intellij.kt.netty.server.socks.handlers

import io.intellij.kt.netty.commons.getLogger
import io.intellij.kt.netty.commons.utils.ChannelUtils.closeOnFlush
import io.intellij.kt.netty.server.socks.handlers.connect.Socks4ServerConnectHandler
import io.intellij.kt.netty.server.socks.handlers.connect.Socks5ServerConnectHandler
import io.intellij.kt.netty.server.socks.handlers.socks5auth.AuthenticateHandler
import io.intellij.kt.netty.server.socks.handlers.socks5auth.Authenticator
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.socksx.SocksMessage
import io.netty.handler.codec.socksx.SocksVersion
import io.netty.handler.codec.socksx.v4.Socks4CommandRequest
import io.netty.handler.codec.socksx.v4.Socks4CommandType
import io.netty.handler.codec.socksx.v5.DefaultSocks5InitialResponse
import io.netty.handler.codec.socksx.v5.Socks5AuthMethod
import io.netty.handler.codec.socksx.v5.Socks5CommandRequest
import io.netty.handler.codec.socksx.v5.Socks5CommandRequestDecoder
import io.netty.handler.codec.socksx.v5.Socks5CommandType
import io.netty.handler.codec.socksx.v5.Socks5InitialRequest
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthRequest
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthRequestDecoder
import kotlin.concurrent.Volatile

/**
 * SocksServerHandler
 *
 * @author tech@intellij.io
 */
@ChannelHandler.Sharable
class SocksServerHandler private constructor(val authenticator: Authenticator) :
    SimpleChannelInboundHandler<SocksMessage>() {

    companion object {
        private val log = getLogger(SocksServerHandler::class.java)

        @Volatile
        private var INSTANCE: SocksServerHandler? = null

        fun getInstance(authenticator: Authenticator): SocksServerHandler? {
            if (INSTANCE == null) {
                synchronized(SocksServerHandler::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = SocksServerHandler(authenticator)
                    }
                }
            }
            return INSTANCE
        }
    }


    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, socksRequest: SocksMessage) {
        when (socksRequest.version()) {
            SocksVersion.SOCKS4a -> {
                val socksV4CmdRequest = socksRequest as Socks4CommandRequest
                if (socksV4CmdRequest.type() === Socks4CommandType.CONNECT) {
                    ctx.pipeline().remove(this)
                    ctx.pipeline().addLast(Socks4ServerConnectHandler())
                    ctx.fireChannelRead(socksV4CmdRequest)
                } else {
                    log.error("Unsupported SOCKS4 command type: {}", socksV4CmdRequest.type())
                    ctx.close()
                }
            }

            SocksVersion.SOCKS5 -> if (socksRequest is Socks5InitialRequest) {
                if (authenticator.isAuthConfigured()) {
                    val pipeline = ctx.pipeline()
                    pipeline.addFirst(Socks5PasswordAuthRequestDecoder())
                    pipeline.addLast(AuthenticateHandler(authenticator))
                    ctx.write(DefaultSocks5InitialResponse(Socks5AuthMethod.PASSWORD))
                } else {
                    ctx.pipeline().addFirst(Socks5CommandRequestDecoder())
                    ctx.write(DefaultSocks5InitialResponse(Socks5AuthMethod.NO_AUTH))
                }
            } else if (socksRequest is Socks5PasswordAuthRequest) {
                // AuthenticateHandler
                ctx.fireChannelRead(socksRequest)
            } else if (socksRequest is Socks5CommandRequest) {
                if (socksRequest.type() === Socks5CommandType.CONNECT) {
                    // 理解链表的特性
                    ctx.pipeline().addLast(Socks5ServerConnectHandler())
                    ctx.pipeline().remove(this)
                    ctx.fireChannelRead(socksRequest)
                } else {
                    log.error("Unsupported SOCKS5 command type: {}", socksRequest.type())
                    ctx.close()
                }
            }

            SocksVersion.UNKNOWN -> {
                log.error("unknown socks version")
                ctx.close()
            }
        }
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.flush()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, throwable: Throwable?) {
        log.error("Exception caught in SocksServerHandler", throwable)
        closeOnFlush(ctx.channel())
    }

}
