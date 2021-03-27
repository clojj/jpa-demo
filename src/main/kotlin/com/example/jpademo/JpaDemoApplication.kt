package com.example.jpademo

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
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

    @GetMapping(path = ["/author/{login}"])
    fun doIt(@PathVariable login: String): ResponseEntity<List<ArticleDTO>> {

        val author = Author(login, "Juergen", "Hoeller")
        val article = Article("Spring Framework 5.0 goes GA", "Dear Spring community ...", "Lorem ipsum", author)
        author.addArticle(article)
        service.save(author)

        service.replaceContent(login, "Lorem JPA")
        val allArticles: List<ArticleDTO> = service.allArticles(login)

        return ResponseEntity.ok(allArticles)
    }
}
