package ru.viklover.homecore.service

import com.fasterxml.jackson.databind.JsonNode

import org.springframework.stereotype.Service

import ru.viklover.homecore.repository.ObjectRepository
import ru.viklover.homecore.exception.homeobject.ObjectNotValidException
import ru.viklover.homecore.exception.homeobject.ObjectParametersNotFound
import ru.viklover.homecore.exception.homeobject.ObjectNotFoundException

@Service
class ObjectService(
    private val objectRepository: ObjectRepository
) {

    fun create(objectClass: String, objectKind: String, jsonNode: JsonNode): Map<String, Any> {

        if (!objectRepository.checkExistingClassAndKind(objectClass, objectKind)) {
            throw ObjectParametersNotFound("One of parameters doesn't exists - class: $objectClass, kind: $objectKind")
        }

        if (!objectRepository.checkValidityObjectFields(jsonNode, objectClass, objectKind)) {
            throw ObjectNotValidException("Object does not match expected fields")
        }

        return objectRepository.createObject(jsonNode, objectClass, objectKind)
    }

    fun update(id: Number, objectKind: String, jsonNode: JsonNode): Map<String, Any> {

        if (!objectRepository.checkExistingObjectById(id)) {
            throw ObjectNotFoundException("Object with id '$id' doesn't exists")
        }

        val objectClass = objectRepository.findObjectClassById(id)

        if (!objectRepository.checkExistingClassAndKind(objectClass, objectKind)) {
            throw ObjectParametersNotFound("One of parameters doesn't exists - class: $objectClass, kind: $objectKind")
        }

        if (!objectRepository.checkValidityObjectFields(jsonNode, objectClass, objectKind, ignoreId = true)) {
            throw ObjectNotValidException("Object does not match expected fields")
        }

        return objectRepository.updateObject(id, jsonNode, objectClass, objectKind)
    }

    fun findAll(): List<Map<String, Any>> {
        return objectRepository.findAll()
    }

    fun findById(id: Number): Map<String, Any> {
        return objectRepository.findById(id)
    }

    fun deleteById(id: Number): Map<String, Any> {
        return objectRepository.deleteById(id)
    }
}