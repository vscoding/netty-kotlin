package io.intellij.kotlin.netty.commons.utils

/**
 * ByteUtils
 *
 * @author tech@intellij.io
 */
object ByteUtils {
    fun getIntBytes(number: Int): ByteArray {
        val byte1 = (number shr 24).toByte()
        val byte2 = (number shr 16).toByte()
        val byte3 = (number shr 8).toByte()
        val byte4 = (number).toByte()
        return byteArrayOf(byte1, byte2, byte3, byte4)
    }
}
