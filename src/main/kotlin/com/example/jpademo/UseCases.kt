package com.example.jpademo

import com.github.michaelbull.result.*
import io.konform.validation.Invalid
import io.konform.validation.Valid
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UseCases(val service: DemoService, val subscription: SubscriptionService) {

    @Bean
    fun addCommentUseCase() = object : AddCommentUseCase {
        override val demoService = service
        override val subscriptionService = subscription
    }

    @Bean
    fun removeCommentUseCase() = object : RemoveCommentUseCase {
        override val demoService = service
    }

    interface RemoveCommentUseCase {
        data class RemoveCommentCommand(val login: String, val id: Long)

        val demoService: DemoService

        fun RemoveCommentCommand.runUseCase(): Result<Unit, DomainError> {
            val command = this
            return binding {
                // TODO val validInput = validate(command).bind()
                asResult { demoService.removeComment(command.login, command.id) }.bind()
            }
        }
    }

    interface AddCommentUseCase {
        data class AddCommentCommand(val login: String, val content: String)

        val demoService: DemoService
        val subscriptionService: SubscriptionService

        private fun validate(addCommentCommand: AddCommentCommand): Result<AddCommentCommand, ValErrors> {
            val validationResult = Validation<AddCommentCommand> {
                AddCommentCommand::login {
                    minLength(3)
                    maxLength(8)
                }
                AddCommentCommand::content {
                    minLength(3)
                }
            }.validate(addCommentCommand)
            return when (validationResult) {
                is Invalid -> Err(ValErrors(validationResult.errors))
                is Valid -> Ok(validationResult.value)
            }
        }

        fun AddCommentCommand.runUseCase(): Result<Long, DomainError> {
            val command = this
            return binding {
                val validInput = validate(command).bind()
                val id = asResult { demoService.addComment(validInput.login, validInput.content).id }.bind()
                if (id != null) {
                    subscriptionService.notifySubscribers("comment added ${validInput.login}")
                    return@binding id
                } else {
                    throw RuntimeException("comment not added")
                }
            }
        }
    }
}

private fun <R> asResult(block: () -> R): Result<R, DomainError> =
    runCatching(block).mapEither({ it }) { GeneralError(it) }

