package ru.viklover.homecore.exception.handler

import java.time.LocalDateTime

data class ExceptionResponse(
    val timestamp: LocalDateTime,
    val message: String,
    val details: String
) {
    companion object {

        fun fromExceptionAndRequest(exceptionMessage: String, details: String): ExceptionResponse {
            return ExceptionResponse(
                    timestamp = LocalDateTime.now(),
                    message = exceptionMessage,
                    details = details
                )
        }
    }
}