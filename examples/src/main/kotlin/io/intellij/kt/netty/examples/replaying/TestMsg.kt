package io.intellij.kt.netty.examples.replaying

/**
 * TestMsg
 *
 * @author tech@intellij.io
 */
data class TestMsg(
    val valid: Boolean,
    val jsonContent: String,
)
