package com.example.jpademo

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JpaDemoApplication

fun main(args: Array<String>) {
    runApplication<JpaDemoApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}
