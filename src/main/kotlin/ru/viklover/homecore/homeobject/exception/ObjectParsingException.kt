package ru.viklover.homecore.homeobject.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

import ru.viklover.homecore.exception.UnprocessableEntityException

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
class ObjectParsingException(cause: String) : UnprocessableEntityException(cause)