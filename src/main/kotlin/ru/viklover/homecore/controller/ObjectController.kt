package ru.viklover.homecore.controller

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.web.bind.annotation.*

import ru.viklover.homecore.service.ObjectService

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

        return objectService.create(objectClass.lowercase(), objectKind.lowercase(), jsonRequestBody)
    }

    @PutMapping("/update")
    fun update(@RequestParam("kind") objectKind: String,
               @RequestBody jsonRequestBody: JsonNode): Map<String, Any> {
        return objectService.update(jsonRequestBody, objectKind)
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