package ru.viklover.homecore.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
open class UnprocessableEntityException(message: String) : RuntimeException(message)