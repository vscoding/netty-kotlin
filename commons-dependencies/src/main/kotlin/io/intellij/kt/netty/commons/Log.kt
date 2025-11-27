package io.intellij.kt.netty.commons

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Log
 *
 * @author tech@intellij.io
 */
fun getLogger(clazz: Class<*>): Logger = LoggerFactory.getLogger(clazz)

fun getLogger(name: String): Logger = LoggerFactory.getLogger(name)