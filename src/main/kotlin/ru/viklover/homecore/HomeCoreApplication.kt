package ru.viklover.homecore

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HomeCoreApplication

fun main(args: Array<String>) {
    runApplication<HomeCoreApplication>(*args)
}
