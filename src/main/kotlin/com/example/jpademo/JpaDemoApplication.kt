package com.example.jpademo

import com.example.jpademo.UseCases.AddCommentUseCase
import com.example.jpademo.UseCases.AddCommentUseCase.AddCommentCommand
import com.example.jpademo.UseCases.RemoveCommentUseCase
import com.example.jpademo.UseCases.RemoveCommentUseCase.RemoveCommentCommand
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapBoth
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

@SpringBootApplication
class JpaDemoApplication

fun main(args: Array<String>) {
    runApplication<JpaDemoApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}

@RestController
class Controller(
    val demoService: DemoService,
    val subscriptionService: SubscriptionService,
    val addCommentUseCase: AddCommentUseCase,
    val removeCommentUseCase: RemoveCommentUseCase
) {

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
    fun addComment(@RequestBody addCommentDTO: AddCommentDTO): Long {
        addCommentUseCase.run {
            AddCommentCommand(addCommentDTO.login, addCommentDTO.content).runUseCase()
        }.mapBoth({ return it }) { throw RuntimeException("$it") }
    }

    @DeleteMapping(path = ["/comment/{id}"])
    fun removeComment(@PathVariable id: Long) {
        removeCommentUseCase.run {
            RemoveCommentCommand("aaa", id).runUseCase()
        }
    }

    // SSE standard only allows 'text/event-stream' !
    @GetMapping(value = ["/subscribe"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    private fun subscribeToMovie(): Subscriber = subscriptionService.subscribe(Subscriber())
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