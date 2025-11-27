package io.intellij.netty.server.socks.handlers.socks5auth

/**
 * Authenticator
 *
 * @author tech@intellij.io
 */
interface Authenticator {
    /**
     * Determines whether authentication is configured by checking if
     * the necessary credentials, such as username and password, are properly set.
     *
     * @return true if authentication credentials are configured; false otherwise
     */
    fun isAuthConfigured(): Boolean

    /**
     * Authenticates a user using the provided username and password credentials.
     *
     * @param username the username of the user attempting to authenticate
     * @param password the password of the user attempting to authenticate
     * @return an `AuthenticateResponse` object containing the authentication result:
     * whether the authentication was successful and a corresponding message
     */
    fun authenticate(username: String, password: String): AuthenticateResponse

}