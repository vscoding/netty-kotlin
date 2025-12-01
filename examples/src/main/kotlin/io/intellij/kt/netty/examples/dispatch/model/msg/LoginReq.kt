package io.intellij.kt.netty.examples.dispatch.model.msg

import com.alibaba.fastjson2.JSON
import io.intellij.kt.netty.examples.dispatch.model.DataBody
import io.intellij.kt.netty.examples.dispatch.model.DataType

/**
 * LoginReq
 *
 * @author tech@intellij.io
 */
data class LoginReq(
    val username: String,
    val password: String,
) {

    fun toDataBody(): DataBody = DataBody(DataType.LOGIN.code, JSON.toJSONString(this))

    companion object {
        fun create(username: String, password: String): DataBody = LoginReq(username, password).toDataBody()
    }

}