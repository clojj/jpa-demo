package com.example.jpademo

import org.springframework.data.jpa.repository.JpaRepository

interface ArticleRepository : JpaRepository<Article, Long> {
    fun findAllByOrderByAddedAtDesc(): Iterable<Article>
}

interface AuthorRepository : JpaRepository<Author, Long> {
    fun findByLogin(login: String): Author?
}