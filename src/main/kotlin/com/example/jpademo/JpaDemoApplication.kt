package com.example.jpademo

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class JpaDemoApplication

fun main(args: Array<String>) {
    runApplication<JpaDemoApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}

@RestController
class Controller(val service: Service) {

    @GetMapping
    fun doIt(): ResponseEntity<List<ArticleDTO>> {

        val juergen = Author("springjuergen", "Juergen", "Hoeller")
        val article = Article("Spring Framework 5.0 goes GA", "Dear Spring community ...", "Lorem ipsum", juergen)
        juergen.articles.add(article)
        service.save(juergen)

        service.updateContent()
        val allArticles: List<ArticleDTO> = service.allArticles("springjuergen")

        return ResponseEntity.ok(allArticles)
    }
}
