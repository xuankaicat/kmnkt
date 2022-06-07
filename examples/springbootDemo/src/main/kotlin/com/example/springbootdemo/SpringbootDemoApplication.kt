package com.example.springbootdemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class SpringbootDemoApplication

fun main(args: Array<String>) {
    runApplication<SpringbootDemoApplication>(*args)
}
