package com.example.jpademo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.QueryHints
import javax.persistence.QueryHint

interface ArticleRepository : JpaRepository<Article, Long> {
    fun findAllByOrderByAddedAtDesc(): Iterable<Article>
}

interface AuthorRepository : JpaRepository<Author, Long> {
    fun findByLogin(login: String): Author?
    fun deleteByLogin(login: String)
}

interface AuthorViewRepository : JpaRepository<AuthorView, Long> {
    @Query("from AuthorView")
    @QueryHints(QueryHint(name = "org.hibernate.cacheable", value ="true"))
    fun findAllAuthors(): List<AuthorView>
}
