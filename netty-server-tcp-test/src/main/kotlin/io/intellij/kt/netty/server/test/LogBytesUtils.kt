package io.intellij.kt.netty.server.test

import io.intellij.kt.netty.commons.getLogger
import org.slf4j.Logger

/**
 * LogBytesUtils
 *
 * @author tech@intellij.io
 */
object LogBytesUtils {
    private val log = getLogger(LogBytesUtils::class.java)

    fun printString(bytes: ByteArray, logger: Logger = log) {
        val str = String(bytes)
        logger.info("Bytes as String: $str")
    }

    fun printHex(bytes: ByteArray, logger: Logger = log) {
        val hex = bytes.joinToString(" ") { "%02X".format(it) }
        logger.info("Bytes as Hex: $hex")
    }

    fun printAscii(bytes: ByteArray, logger: Logger = log) {
        val ascii = bytes.map {
            if (it in 32..126) it.toInt().toChar() else '.'
        }.joinToString("")
        logger.info("Bytes as ASCII: $ascii")
    }

    fun printBytes(bytes: ByteArray, logger: Logger = log) {
        val count = bytes.size
        logger.info("Bytes count: $count")
        printHex(bytes, logger)
        printString(bytes, logger)
        printAscii(bytes, logger)
    }

}