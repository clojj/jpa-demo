package com.example.jpademo

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DemoService(val authorRepository: AuthorRepository, val authorViewRepository: AuthorViewRepository) {

    fun findAuthorByLogin(login: String) =
        authorRepository.findByLogin(login)

    fun findAuthorCommentsByLogin(login: String) =
        authorRepository.findByLogin(login)?.getComments()?.map { CommentDTO(it.content) }

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
        authorViewRepository.findAll()
}