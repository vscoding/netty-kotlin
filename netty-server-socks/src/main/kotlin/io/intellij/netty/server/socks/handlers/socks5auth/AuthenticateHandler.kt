package io.intellij.netty.server.socks.handlers.socks5auth

import io.intellij.kotlin.netty.commons.getLogger
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.socksx.v5.DefaultSocks5PasswordAuthResponse
import io.netty.handler.codec.socksx.v5.Socks5CommandRequestDecoder
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthRequest
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthRequestDecoder
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthStatus

/**
 * AuthenticateHandler
 *
 * @author tech@intellij.io
 */
class AuthenticateHandler(
    private val authenticator: Authenticator
) : SimpleChannelInboundHandler<Socks5PasswordAuthRequest>() {

    private val log = getLogger(AuthenticateHandler::class.java)

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, authRequest: Socks5PasswordAuthRequest) {
        val username = authRequest.username()
        val password = authRequest.password()
        val authenticateResponse = authenticator.authenticate(username, password)
        if (authenticateResponse.success) {
            ctx.pipeline().remove(Socks5PasswordAuthRequestDecoder::class.java)
            ctx.pipeline().addFirst(Socks5CommandRequestDecoder())
            ctx.pipeline().remove(this)
            ctx.writeAndFlush(DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus.SUCCESS))
        } else {
            log.error("Authentication failed: {}", authenticateResponse.message)
            ctx.pipeline().remove(this)
            ctx.writeAndFlush(DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus.FAILURE))
                .addListener(ChannelFutureListener.CLOSE)
        }
    }
}
