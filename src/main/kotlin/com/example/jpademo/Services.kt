package com.example.jpademo

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class Service(val authorRepository: AuthorRepository) {

    fun updateContent() {
        val user = authorRepository.findByLogin("springjuergen")
        user?.articles?.first { it.content.contains("Lorem") }?.content = "Kotlin JPA!"
    }

    fun allArticles(login: String): List<ArticleDTO> {
        val user = authorRepository.findByLogin("springjuergen")
        return if (user != null)
            user.articles.map { ArticleDTO(it.author.login, it.content) }
        else emptyList()
    }

    fun save(author: Author) {
        authorRepository.save(author)
    }

    fun delete(login: String) {
        authorRepository.deleteByLogin(login)
    }
}