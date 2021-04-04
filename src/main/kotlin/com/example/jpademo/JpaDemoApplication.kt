package com.example.jpademo

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@SpringBootApplication
class JpaDemoApplication

fun main(args: Array<String>) {
    runApplication<JpaDemoApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}

@RestController
class Controller(val demoService: DemoService, val subscriptionService: SubscriptionService) {

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

    @PostMapping(path = ["/comment"])
    fun addComment(@RequestBody addComment: AddComment) {
        demoService.addComment(addComment.login, addComment.content)
    }

    @GetMapping(value = ["/subscribe"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    private fun subscribeToMovie(): Subscriber = subscriptionService.subscribe(Subscriber())

}

data class AddComment(val login: String, val content: String)

class Subscriber : SseEmitter(Long.MAX_VALUE)