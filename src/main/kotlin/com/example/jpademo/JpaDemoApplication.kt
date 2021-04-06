package com.example.jpademo

import com.github.michaelbull.result.*
import io.konform.validation.Invalid
import io.konform.validation.Valid
import io.konform.validation.Validation
import io.konform.validation.ValidationErrors
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.lang.RuntimeException

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
    fun addComment(@RequestBody addCommentDTO: AddCommentDTO) {
        val r: Result<Boolean, DomainError> = binding {
            val validInput = validateAddCommentDto(addCommentDTO).bind()
            val saved = asResult { demoService.addComment(validInput.login, validInput.content) }.bind()
            val notified = subscriptionService.notifySubscribers("comment added ${validInput.login}").bind()
            true
        }
        r.mapBoth({ "Ok" }) {
            throw RuntimeException("$it")
        }
    }

    // SSE standard only allows 'text/event-stream' !
    @GetMapping(value = ["/subscribe"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    private fun subscribeToMovie(): Subscriber = subscriptionService.subscribe(Subscriber())

    private fun asResult(block: () -> Unit): Result<Boolean, DomainError> =
        runCatching(block).mapEither({ true }) { GeneralError(it) }
}

data class AddCommentDTO(val login: String, val content: String)

sealed class DomainError
object LoginNotFund : DomainError()
data class ValErrors(val validationErrors: ValidationErrors) : DomainError()
data class GeneralError(val throwable: Throwable) : DomainError()

fun validateAddCommentDto(addCommentDTO: AddCommentDTO): Result<AddCommentDTO, ValErrors> {
    val validationResult = Validation<AddCommentDTO> {
        AddCommentDTO::login {
            minLength(3)
            maxLength(8)
        }
        AddCommentDTO::content {
            minLength(3)
        }
    }.validate(addCommentDTO)
    return when (validationResult) {
        is Invalid -> Err(ValErrors(validationResult.errors))
        is Valid -> Ok(validationResult.value)
    }
}

class Subscriber : SseEmitter(Long.MAX_VALUE)