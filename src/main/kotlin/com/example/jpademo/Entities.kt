package com.example.jpademo

import java.time.LocalDateTime
import javax.persistence.*

// TODO equals by id ?

@Entity
class Author(
    var login: String,
    var firstname: String,
    var lastname: String,
    var description: String? = null,
    @OneToMany(mappedBy = "author", cascade = [CascadeType.ALL], orphanRemoval = true)
    var articles: MutableList<Article> = mutableListOf(),
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
)

@Entity
class Article(
    var title: String,
    var headline: String,
    var content: String,
    @ManyToOne(fetch = FetchType.LAZY)
    var author: Author,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var addedAt: LocalDateTime = LocalDateTime.now()
)

data class ArticleDTO(val login: String, val content: String)