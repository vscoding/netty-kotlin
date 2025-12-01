package io.intellij.kt.netty.examples.dispatch.model.msg

import com.alibaba.fastjson2.JSON
import io.intellij.kt.netty.examples.dispatch.model.DataBody
import io.intellij.kt.netty.examples.dispatch.model.DataType

/**
 * Response
 *
 * @author tech@intellij.io
 */
data class Response(
    val code: Int,
    val msg: String
) {
    companion object {
        fun create(code: Int, msg: String): DataBody {
            return DataBody(DataType.RESPONSE.code, JSON.toJSONString(Response(code, msg)))
        }
    }
}