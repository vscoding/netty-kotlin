package io.intellij.kt.netty.tcpfrp.server

import com.alibaba.fastjson2.JSON
import org.junit.jupiter.api.Test

/**
 * DataClassJsonTest
 *
 * @author tech@intellij.io
 */
class DataClassJsonTest {

    data class Person(
        val name: String,
        val age: Int
    )

    @Test
    fun `test fastjson2`() {
        println("json = ${JSON.toJSONString(Person("John", 18))}")
    }

}