package com.example.jpademo

import org.springframework.data.jpa.repository.JpaRepository

interface ArticleRepository : JpaRepository<Article, Long> {
    fun findAllByOrderByAddedAtDesc(): Iterable<Article>
}

interface UserRepository : JpaRepository<User, Long> {
    fun findByLogin(login: String): User?
}