package ru.viklover.homecore.repository

import com.fasterxml.jackson.databind.JsonNode

import org.springframework.stereotype.Service
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.transaction.annotation.Transactional

import ru.viklover.homecore.util.JdbcUtil
import ru.viklover.homecore.util.ObjectFieldsUtil
import ru.viklover.homecore.exception.homeobject.ObjectNotFoundException

@Service
@CacheConfig(cacheNames = ["findById"])
class ObjectRepository(
    private val jdbcTemplate: JdbcTemplate,
    private val jdbcUtil: JdbcUtil,
    private val objectFieldsUtil: ObjectFieldsUtil
) {

    companion object {
        val BASE_OBJECT_FIELDS = arrayOf("id", "type", "class", "kind", "created_at", "updated_at", "removed")
        val BASE_OBJECT_FIELDS_OF_LAYERS = arrayOf("object_id")
    }

    fun findObjectInfoById(id: Number): Array<String> {

        val list: MutableList<MutableMap<String, Any>> = jdbcTemplate.queryForList(
            "select type, class, kind from object where id = ? limit 1", id
        )

        if (list.size == 0)
            throw ObjectNotFoundException("Object with id: '$id' is not found")

        val dictionary = list[0]

        return arrayOf(dictionary["type"].toString(),
                       dictionary["class"].toString(),
                       dictionary["kind"].toString())
    }

    fun findObjectTypeAndKindById(id: Number): Array<String> {

        val list: MutableList<MutableMap<String, Any>> = jdbcTemplate.queryForList(
            "select type, kind from object where id = ? limit 1", id
        )

        if (list.size == 0)
            throw ObjectNotFoundException("Object with id: '$id' is not found")

        val dictionary = list[0]

        return arrayOf(dictionary["type"].toString(), dictionary["kind"].toString())
    }

    fun findObjectTypeByClass(objectClass: String): String {
        return jdbcTemplate.queryForObject(
            "select type from object_class where class = ? limit 1", String::class.java, objectClass
        )
    }

    fun findObjectClassById(id: Number): String {
        return jdbcTemplate.queryForObject(
            "select class from object where id = ? limit 1", String::class.java, id
        )
    }


    fun checkExistingClassAndKind(objectClass: String, objectKind: String): Boolean {
        return jdbcTemplate.queryForObject(
            "select exists(select class, kind from object_kind where class = ? and kind = ?)",
            Boolean::class.java,
            objectClass, objectKind
        )
    }

    fun checkExistingObjectById(id: Number): Boolean {
        return jdbcTemplate.queryForObject("select exists(select * from object where id = ?)", Boolean::class.java, id)
    }

    fun checkValidityObjectFields(jsonNode: JsonNode, objectClass: String, objectKind: String,
                                  ignoreId: Boolean = false): Boolean {

        val currentFields = jsonNode.fieldNames().asSequence().toList()

        val expectedFields = jdbcUtil.getColumnsWithInfoByTables(
            "object", findObjectTypeByClass(objectClass), objectClass, "${objectClass}__${objectKind}"
        )

        expectedFields.forEach { (column, columnType, isNullable) ->

            if (BASE_OBJECT_FIELDS.contains(column) || BASE_OBJECT_FIELDS_OF_LAYERS.contains(column)) {

                if (column == "id" && ignoreId && currentFields.contains(column)) {
                    return@forEach
                }

                if (currentFields.contains(column))
                    return false

                return@forEach
            }

            if ((!currentFields.contains(column) && !isNullable) ||
                (currentFields.contains(column) &&
                    !objectFieldsUtil.compare(jsonNode[column].nodeType, columnType, isNullable))) {
                return false
            }
        }

        return true
    }


    @Transactional
    fun createObject(jsonNode: JsonNode, objectClass: String, objectKind: String): Map<String, Any> {

        val objectType = findObjectTypeByClass(objectClass)

        val objectFields = objectFieldsUtil.toMapFromJsonWithInfo(jsonNode, objectType, objectClass, objectKind)


        // PREPARING VARIABLES FOR FUTURE QUERIES
        var columnsTable: List<String>
        var preparedFields: Map<String, Any?>


        // INSERT INTO OBJECT TABLE WITH GETTING NEW ID OBJECT
        val keyHolder = GeneratedKeyHolder()

        columnsTable = jdbcUtil.getColumnsListByTable("object")
        preparedFields = objectFields.filterKeys { columnsTable.contains(it) }

        jdbcUtil.insertIntoWithKeyHolder("object", preparedFields, keyHolder)

        objectFields["object_id"] = keyHolder.key


        // INSERT INTO OBJECT LAYERS TABLES WITH USING 'object_id' AS FOREIGN KEY
        arrayListOf(objectType, objectClass).apply {

            if (objectKind != "default")
                add("${objectClass}__$objectKind")

            forEach { table ->

                columnsTable = jdbcUtil.getColumnsListByTable(table)
                preparedFields = objectFields.filterKeys { columnsTable.contains(it) }

                jdbcUtil.insertInto(table, preparedFields)
            }
        }

        return findById(keyHolder.key!!)
    }

    @Transactional
    @CachePut(value = ["findById"], key = "#id")
    fun updateObject(id: Number, jsonNode: JsonNode, objectClass: String, objectKind: String): Map<String, Any> {

        val (objectType, currentObjectKind) = findObjectTypeAndKindById(id)

        val objectFields = objectFieldsUtil.toMapFromJsonWithInfo(jsonNode, objectType, objectClass, objectKind)

        var columnsTable: List<String>
        var updatedFields: Map<String, Any?>
        var conditions: Map<String, Any?>

        arrayListOf("object", objectType, objectClass).onEach { table ->

            columnsTable = jdbcUtil.getColumnsListByTable(table)
            updatedFields = objectFields.filterKeys { columnsTable.contains(it) }

            conditions = if (table == "object")
                mapOf("id" to id)
            else
                mapOf("object_id" to id)

            jdbcUtil.update(table, updatedFields, conditions)
        }

        if (currentObjectKind != objectKind) {
            objectFields["object_id"] = id

            if (currentObjectKind != "default")
                jdbcUtil.delete("${objectClass}__$currentObjectKind", mapOf("object_id" to id))

            columnsTable = jdbcUtil.getColumnsListByTable("${objectClass}__$objectKind")
            updatedFields = objectFields.filterKeys { columnsTable.contains(it) }

            jdbcUtil.insertInto("${objectClass}__$objectKind", updatedFields)
        }

        return findById(id)
    }

    @Cacheable(value = ["findById"], key = "#id")
    fun findById(id: Number): Map<String, Any> {

        val (objectType, objectClass, objectKind) = findObjectInfoById(id)

        val query = buildString {

            append("SELECT * FROM object INNER JOIN $objectType t on object.id = t.object_id ")
            append("INNER JOIN $objectClass c on object.id = c.object_id ")

            if (objectKind != "default")
                append("INNER JOIN ${objectClass}__$objectKind k on object.id = k.object_id ")

            append("WHERE id = ?")
        }

        val list = jdbcTemplate.queryForList(query, id)

        if (list.size == 0)
            throw ObjectNotFoundException("Object with id: '$id' is not found")

        return list[0].filter {
            !it.key.contains("object_id")
        }
    }

    @Transactional
    fun findAll(): List<Map<String, Any>> {
        return jdbcTemplate.queryForList("select id from object order by id", Int::class.java).map { id ->
            findById(id)
        }
    }

    @Transactional
    fun deleteById(id: Number): Map<String, Any> {
        jdbcTemplate.update("update object set removed = true where id = ?", id)
        return findById(id)
    }
}
