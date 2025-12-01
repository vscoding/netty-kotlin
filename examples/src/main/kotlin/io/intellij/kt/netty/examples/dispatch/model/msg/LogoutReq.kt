package io.intellij.kt.netty.examples.dispatch.model.msg

import com.alibaba.fastjson2.JSON
import io.intellij.kt.netty.examples.dispatch.model.DataBody
import io.intellij.kt.netty.examples.dispatch.model.DataType

/**
 * LogoutReq
 *
 * @author tech@intellij.io
 */
data class LogoutReq(
    val username: String
) {

    companion object {
        fun create(username: String): DataBody {
            return DataBody(DataType.LOGOUT.code, JSON.toJSONString(LogoutReq(username)))
        }
    }
}