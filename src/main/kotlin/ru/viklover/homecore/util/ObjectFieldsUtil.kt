package ru.viklover.homecore.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeType

import org.springframework.stereotype.Component

@Component
class ObjectFieldsUtil {

    fun compare(jsonType: JsonNodeType, sqlType: String, nullable: Boolean): Boolean {

        return when (jsonType) {
            JsonNodeType.NUMBER -> {
                return arrayOf("integer", "double precision").contains(sqlType)
            }
            JsonNodeType.BOOLEAN -> {
                return arrayOf("boolean").contains(sqlType)
            }
            JsonNodeType.STRING -> {
                return arrayOf("character varying", "text").contains(sqlType)
            }
            JsonNodeType.NULL -> {
                return nullable
            }
            else -> false
        }
    }

    fun toMapFromJson(jsonNode: JsonNode): MutableMap<String, Any?> {

        val jsonObject = HashMap<String, Any?>()

        jsonNode.fields().forEach {

            if (it.value.isDouble) {
                jsonObject[it.key] = it.value.asDouble()
            } else if (it.value.isNumber) {
                jsonObject[it.key] = it.value.asInt()
            } else if (it.value.isBoolean) {
                jsonObject[it.key] = it.value.asBoolean()
            } else if (it.value.isNull) {
                jsonObject[it.key] = null
            } else {
                jsonObject[it.key] = it.value.asText()
            }
        }

        return jsonObject
    }

    fun toMapFromJsonWithInfo(jsonNode: JsonNode,
                              objectType: String,
                              objectClass: String,
                              objectKind: String): MutableMap<String, Any?> {

        return toMapFromJson(jsonNode)
            .plus(mapOf("type" to objectType,
                        "class" to objectClass,
                        "kind" to objectKind))
            .filterValues { it != null }
            .toMutableMap()
    }
}