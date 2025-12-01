package io.intellij.kt.netty.spring.boot.config

import io.intellij.kt.netty.commons.getLogger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

/**
 * ControllerExceptionHandler
 *
 * @author tech@intellij.io
 */
@ControllerAdvice
class ControllerExceptionHandler {
    companion object {
        private val log = getLogger(ControllerExceptionHandler::class.java)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<Map<String, Any?>> {
        log.error("handleException", e)
        return ResponseEntity.ok(mapOf("code" to 500, "msg" to e.message))
    }

}