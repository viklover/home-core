package ru.viklover.homecore.util

import org.springframework.jdbc.core.ArgumentPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.query
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Service

@Service
class JdbcUtil(
    private val jdbcTemplate: JdbcTemplate
) {

    fun getColumnsListByTable(table: String): MutableList<String> {
        return jdbcTemplate.queryForList(
            "select column_name from information_schema.columns where table_name = ?",
            String::class.java,
            table
        )
    }

    fun getColumnsWithInfoByTables(vararg tables: String): List<Triple<String, String, Boolean>> {
        return jdbcTemplate.query(
            "select column_name, data_type, (is_nullable = 'YES') as is_nullable from information_schema.columns " +
                    "where table_name in (${(tables.indices).joinToString(", ") { "?" }})", *tables
        ) { column, _ ->

            return@query Triple(
                column.getString("column_name"),
                column.getString("data_type"),
                column.getBoolean("is_nullable")
            )
        }
    }

    fun insertIntoWithKeyHolder(table: String, fields: Map<String, Any?>, keyHolder: GeneratedKeyHolder) {

        jdbcTemplate.update({ connection ->

            val statement = connection.prepareStatement("insert into $table " +
                    "(${fields.keys.joinToString(", ") { it }}) values " +
                    "(${(fields.values.indices).joinToString(", ") { "?" }})", arrayOf("id")
            )

            val preparedStatementSetter = ArgumentPreparedStatementSetter(fields.values.toTypedArray())
            preparedStatementSetter.setValues(statement)

            return@update statement

        }, keyHolder)
    }

    fun insertInto(table: String, fields: Map<String, Any?>) {

        if (fields.isEmpty())
            return

        val query = "insert into $table " +
                "(${fields.keys.joinToString(", ")}) values " +
                "(${fields.values.joinToString(", ") { "?" }})"

        jdbcTemplate.update(query, *fields.values.toTypedArray())
    }

    fun delete(table: String, conditions: Map<String, Any?>) {

        val query = "delete from $table where " +
                conditions.keys.joinToString(" and ") { "$it = ?" }

        jdbcTemplate.update(query, *conditions.values.toTypedArray())
    }

    fun update(table: String, updatedFields: Map<String, Any?>, conditions: Map<String, Any?>) {

        if (updatedFields.isEmpty())
            return

        val query = "update $table set " +
                "${updatedFields.keys.joinToString(", ") { "$it = ?" } } where " +
                conditions.keys.joinToString(" and ") {"$it = ?" }

        jdbcTemplate.update(query, *updatedFields.values.toTypedArray(), *conditions.values.toTypedArray())
    }
}