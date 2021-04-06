package com.example.jpademo

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapEither
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DemoService(
    val authorRepository: AuthorRepository,
    val authorViewRepository: AuthorViewRepository
) {

    fun findAuthorByLogin(login: String) =
        authorRepository.findByLogin(login)

    fun findAuthorCommentsByLogin(login: String) =
        authorRepository.findByLogin(login)?.getComments()?.map { CommentDTO(it.content) }

    fun addComment(login: String, content: String) {
        authorRepository.findByLogin(login)?.addComment(content)
    }

    fun replaceContent(login: String, content: String) {
        val author = authorRepository.findByLogin(login)
        author?.getArticles()?.forEach { it.content = content }
    }

    fun allArticles(login: String): List<ArticleDTO> {
        val author = authorRepository.findByLogin(login)
        return if (author != null)
            author.getArticles().map { ArticleDTO(it.author.login, it.title, it.content) }
        else emptyList()
    }

    fun save(author: Author) {
        authorRepository.save(author)
    }

    fun delete(login: String) {
        authorRepository.deleteByLogin(login)
    }

    fun deleteArticle(login: String, title: String) {
        val author = authorRepository.findByLogin(login)
        author?.let {
            val article = author.getArticles().first { it.title == title }
            author.removeArticle(article)
        }
    }

    fun allAuthors(): List<AuthorView> =
        // TODO ehcache
        authorViewRepository.findAllAuthors()
}

interface SubscriptionService {

    fun subscribe(subscriber: Subscriber): Subscriber

    fun notifySubscribers(message: String): Result<Boolean, DomainError>
}

@Service
class DefaultSubscriptionService : SubscriptionService {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(DefaultSubscriptionService::class.java)
        val subscribers: MutableSet<Subscriber> = hashSetOf()
    }

    override fun subscribe(subscriber: Subscriber): Subscriber {
        subscribers.add(subscriber)
        return subscriber
    }

    override fun notifySubscribers(message: String): Result<Boolean, DomainError> =
        com.github.michaelbull.result.runCatching {
            subscribers.forEach { subscriber ->
                subscriber.send(message)
                subscriber.onError { error ->
                    logger.info("Seems the subscriber has already dropped out. Remove it from the list")
                    subscriber.completeWithError(error)
                    subscribers.remove(subscriber)
                }
            }
        }.mapEither({ true }) { GeneralError(it) }
}