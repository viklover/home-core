package ru.viklover.homecore.homeobject.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

import ru.viklover.homecore.exception.BadRequestException

@ResponseStatus(HttpStatus.BAD_REQUEST)
class ObjectParametersNotFound(message: String) : BadRequestException(message)