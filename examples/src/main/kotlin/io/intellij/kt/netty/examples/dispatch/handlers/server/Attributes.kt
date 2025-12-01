package io.intellij.kt.netty.examples.dispatch.handlers.server

import io.netty.util.AttributeKey

/**
 * Attributes
 *
 * @author tech@intellij.io
 */
object Attributes {
    val USERNAME = AttributeKey.newInstance<String>("username")
}