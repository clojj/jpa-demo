package com.example.jpademo

import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Article(
    var title: String,
    var headline: String,
    var content: String,
    @ManyToOne var author: User,
    @Id @GeneratedValue var id: Long? = null,
    var addedAt: LocalDateTime = LocalDateTime.now()
)

@Entity
class User(
    var login: String,
    var firstname: String,
    var lastname: String,
    var description: String? = null,
    @OneToMany var articles: MutableList<Article> = mutableListOf(),
    @Id @GeneratedValue var id: Long? = null
)