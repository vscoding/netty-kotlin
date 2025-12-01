package io.intellij.kt.netty.examples.dispatch.model

/**
 * DataBody
 *
 * @author tech@intellij.io
 */
data class DataBody(
    val dataType: Int,
    val json: String
)

enum class DataType(
    val code: Int,
    val desc: String
) {
    LOGIN(1, "login"),
    LOGOUT(2, "logout"),
    RESPONSE(3, "response");
}