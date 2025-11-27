package io.intellij.kotlin.netty.commons

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Log
 *
 * @author tech@intellij.io
 */
fun getLogger(forClass: Class<*>): Logger = LoggerFactory.getLogger(forClass)