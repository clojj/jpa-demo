package com.example.jpademo

import java.time.LocalDateTime
import javax.persistence.*

// TODO equals by id ?

@Entity
class Author(
    @Column(unique = true)
    var login: String,
    var firstname: String,
    var lastname: String,
    var description: String? = null,

    @OneToMany(
        fetch = FetchType.EAGER,
        mappedBy = "author",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    private var articles: MutableList<Article> = mutableListOf(),

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "author",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    private var comments: MutableList<Comment> = mutableListOf(),

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

    // TODO generic getter, add, remove

    fun getComments() = comments

    fun addComment(comment: Comment) {
        comments.add(comment)
        comment.author = this
    }

    fun removeComment(comment: Comment) {
        comments.remove(comment)
        comment.author = this
    }
}

@Entity
class Article(
    var title: String,
    var headline: String,
    var content: String,

    // @ManyToOne(fetch = FetchType.LAZY)
    @ManyToOne
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

data class ArticleDTO(val login: String, val title: String, val content: String)@Entity

class Comment(
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

        other as Comment

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

data class CommentDTO(val content: String)