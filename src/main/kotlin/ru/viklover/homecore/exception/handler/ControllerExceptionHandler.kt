package ru.viklover.homecore.exception.handler

import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

import ru.viklover.homecore.exception.BadRequestException
import ru.viklover.homecore.exception.PreconditionFailedException
import ru.viklover.homecore.exception.UnprocessableEntityException

@Component
@RestControllerAdvice
@Profile("!development")
class ControllerExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(exception: Exception, request: WebRequest): ResponseEntity<ExceptionResponse> {
        return ResponseEntity(
            ExceptionResponse.fromExceptionAndRequest(
                exception.message!!,
                request.getDescription(false)
            ), HttpStatus.INTERNAL_SERVER_ERROR
        )
    }

    @ExceptionHandler(BadRequestException::class)
    fun badRequestException(exception: Exception, request: WebRequest): ResponseEntity<ExceptionResponse> {
        return ResponseEntity(
            ExceptionResponse.fromExceptionAndRequest(
                exception.message!!,
                request.getDescription(false)
            ), HttpStatus.BAD_REQUEST
        )
    }


    @ExceptionHandler(PreconditionFailedException::class)
    fun preconditionFailedException(exception: Exception, request: WebRequest): ResponseEntity<ExceptionResponse> {
        return ResponseEntity(
            ExceptionResponse.fromExceptionAndRequest(
                exception.message!!,
                request.getDescription(false)
            ), HttpStatus.PRECONDITION_FAILED
        )
    }


    @ExceptionHandler(UnprocessableEntityException::class)
    fun unprocessableEntityException(exception: Exception, request: WebRequest): ResponseEntity<ExceptionResponse> {
        return ResponseEntity(
            ExceptionResponse.fromExceptionAndRequest(
                exception.message!!,
                request.getDescription(false)
            ), HttpStatus.UNPROCESSABLE_ENTITY
        )
    }
}