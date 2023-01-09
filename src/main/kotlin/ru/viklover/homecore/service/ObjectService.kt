package ru.viklover.homecore.service

import org.springframework.stereotype.Service
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.contains
import ru.viklover.homecore.exception.homeobject.ObjectNotFoundException

import ru.viklover.homecore.repository.ObjectRepository
import ru.viklover.homecore.exception.homeobject.ObjectNotValidException
import ru.viklover.homecore.exception.homeobject.ObjectParametersNotFound
import ru.viklover.homecore.exception.homeobject.ObjectParsingException

@Service
class ObjectService(
    private val objectRepository: ObjectRepository
) {

    fun create(objectClass: String, objectKind: String, jsonNode: JsonNode): Map<String, Any> {

        if (!jsonNode.isObject) {
            throw ObjectParsingException("JSON Object is expected in the request body!")
        }

        if (!objectRepository.checkExistingClassAndKind(objectClass, objectKind)) {
            throw ObjectParametersNotFound("One of parameters doesn't exists - class: $objectClass, kind: $objectKind")
        }

        if (!objectRepository.checkValidityObjectFields(jsonNode, objectClass, objectKind)) {
            throw ObjectNotValidException("Object does not match expected fields")
        }

        return objectRepository.createObject(jsonNode, objectClass, objectKind)
    }

    fun update(jsonNode: JsonNode, objectKind: String): Map<String, Any> {

        if (!jsonNode.isObject) {
            throw ObjectParsingException("JSON Object is expected in the request body!")
        }

        if (!jsonNode.contains("id")) {
            throw ObjectNotValidException("'id' is not found in request body")
        } else if (!jsonNode["id"].isNumber) {
            throw ObjectNotValidException("'id' field has to be number type: current type is ${jsonNode["id"].nodeType}")
        }

        val objectId = jsonNode["id"].asLong()

        if (!objectRepository.checkExistingObjectById(objectId)) {
            throw ObjectNotFoundException("Object with id '$objectId' doesn't exists")
        }

        val objectClass = objectRepository.findObjectClassById(objectId)

        if (!objectRepository.checkExistingClassAndKind(objectClass, objectKind)) {
            throw ObjectParametersNotFound("One of parameters doesn't exists - class: $objectClass, kind: $objectKind")
        }

        if (!objectRepository.checkValidityObjectFields(jsonNode, objectClass, objectKind, ignoreId = true)) {
            throw ObjectNotValidException("Object does not match expected fields")
        }

        return objectRepository.updateObject(jsonNode, objectClass, objectKind)
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