package io.intellij.kt.netty.tcpfrp.server

import com.alibaba.fastjson2.JSON
import org.junit.jupiter.api.Test

/**
 * DataClassJsonTest
 *
 * @author tech@intellij.io
 */
class JsonTest {

    data class Person(
        val name: String,
        val age: Int
    )

    @Test
    fun `test serialize data class`() {
        println("json = ${JSON.toJSONString(Person("John", 18))}")
    }

}