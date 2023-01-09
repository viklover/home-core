package ru.viklover.homecore.exception.homeobject

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

import ru.viklover.homecore.exception.PreconditionFailedException

@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
class ObjectNotValidException(message: String) : PreconditionFailedException(message)