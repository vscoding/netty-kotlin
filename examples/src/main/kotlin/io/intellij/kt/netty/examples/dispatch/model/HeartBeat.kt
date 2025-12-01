package io.intellij.kt.netty.examples.dispatch.model

import java.util.Date

/**
 * HeartBeat
 *
 * @author tech@intellij.io
 */
data class HeartBeat(
    val time: Date,
    val id: String,
    val seq: Long
) {
    override fun toString(): String {
        return "HeartBeat(time=$time, id='$id', seq=$seq)"
    }
}