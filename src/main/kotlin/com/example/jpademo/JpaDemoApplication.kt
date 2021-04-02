package com.example.jpademo

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@EnableScheduling
class JpaDemoApplication

fun main(args: Array<String>) {
    runApplication<JpaDemoApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}

@Service
class CronService {
    @Scheduled(fixedDelay = 10000)
    fun taskAssignment() {
        val log: Logger = LoggerFactory.getLogger("Task Assignment")
        log.info("assign...")
    }
}

@RestController
class Controller(val demoService: DemoService) {

    @GetMapping(path = ["/author/{login}"])
    fun doIt(@PathVariable login: String): ResponseEntity<List<ArticleDTO>> {

        val author = Author(login, "Juergen", "Hoeller")
        val article = Article("Spring Framework 5.0 goes GA", "Dear Spring community ...", "Lorem ipsum", author)
        author.addArticle(article)
        demoService.save(author)

        demoService.replaceContent(login, "Lorem JPA")
        val allArticles: List<ArticleDTO> = demoService.allArticles(login)

        return ResponseEntity.ok(allArticles)
    }

    // TODO AuthorViewDTO

    @GetMapping(path = ["/authors"])
    fun authors(): ResponseEntity<List<AuthorView>> {

        val allAuthors = demoService.allAuthors()

        return ResponseEntity.ok(allAuthors)
    }
}
