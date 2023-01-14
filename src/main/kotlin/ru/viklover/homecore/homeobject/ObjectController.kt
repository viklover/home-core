package ru.viklover.homecore.homeobject

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.contains

import ru.viklover.homecore.homeobject.exception.ObjectNotValidException
import ru.viklover.homecore.homeobject.exception.ObjectParsingException

import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/objects")
class ObjectController(
    private val objectService: ObjectService
) {

    @PostMapping("/create")
    fun create(@RequestParam("class") objectClass: String,
               @RequestParam("kind") objectKind: String,
               @RequestBody jsonRequestBody: JsonNode): Map<String, Any> {

        if (!jsonRequestBody.isObject) {
            throw ObjectParsingException("JSON Object is expected in the request body!")
        }

        return objectService.create(objectClass.lowercase(), objectKind.lowercase(), jsonRequestBody)
    }

    @PutMapping("/update")
    fun update(@RequestParam("kind") objectKind: String,
               @RequestBody jsonRequestBody: JsonNode): Map<String, Any> {

        if (!jsonRequestBody.isObject) {
            throw ObjectParsingException("JSON Object is expected in the request body!")
        }

        if (!jsonRequestBody.contains("id")) {
            throw ObjectNotValidException("'id' is not found in request body")
        }
        else if (!jsonRequestBody["id"].isNumber) {
            throw ObjectNotValidException("'id' field has to be number type: current type is ${jsonRequestBody["id"].nodeType}")
        }

        return objectService.update(jsonRequestBody["id"].asLong(), objectKind, jsonRequestBody)
    }

    @GetMapping()
    fun findAll(): List<Map<String, Any>> {
        return objectService.findAll()
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable("id") id: Number): Map<String, Any> {
        return objectService.findById(id)
    }

    @DeleteMapping("/{id}")
    fun deleteById(@PathVariable("id") id: Number): Map<String, Any> {
        return objectService.deleteById(id)
    }
}