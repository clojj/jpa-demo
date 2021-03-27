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

    @OneToMany(
        mappedBy = "author",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    private var articles: MutableList<Article> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
) {
    fun getArticles() = articles

    fun addArticle(article: Article) {
        articles.add(article)
        article.author = this
    }

    fun removeArticle(article: Article) {
        articles.remove(article)
        article.author = this
    }
}

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
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Article

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

data class ArticleDTO(val login: String, val title: String, val content: String)