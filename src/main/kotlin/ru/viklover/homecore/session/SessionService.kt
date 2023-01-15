package ru.viklover.homecore.session

import org.springframework.context.annotation.Bean
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Service

@Service
class SessionService(
    private val jdbcTemplate: JdbcTemplate
) {

    private lateinit var sessionId: Number

    @Bean
    fun startSession() {

        val generatedKeyHolder = GeneratedKeyHolder()

        jdbcTemplate.update({ connection ->
            return@update connection.prepareStatement(
                "INSERT INTO session DEFAULT VALUES",
                arrayOf("id"))
        }, generatedKeyHolder)

        sessionId = generatedKeyHolder.key!!
    }

    fun getSessionId(): Number {
        return sessionId
    }
}