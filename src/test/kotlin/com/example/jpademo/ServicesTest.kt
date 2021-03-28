package com.example.jpademo

import org.assertj.core.api.Assertions.assertThat
import org.hibernate.LazyInitializationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ServicesTest @Autowired constructor(
    val service: Service
) {

    @Test
    fun all() {
        service.delete("springjuergen")

        val juergen = Author("springjuergen", "Juergen", "Hoeller")
        juergen.addArticle(Article("Spring", "Dear Spring community ...", "Lorem ipsum", juergen))
        juergen.addArticle(Article("JPA", "Dear JPA community ...", "Lorem ipsum", juergen))
        service.save(juergen)

        service.replaceContent("springjuergen", "Lorem JPA")

        val allArticles = service.allArticles("springjuergen")
        assertThat(allArticles).containsExactly(
            ArticleDTO("springjuergen", "Spring", "Lorem JPA"),
            ArticleDTO("springjuergen", "JPA", "Lorem JPA")
        )

        service.deleteArticle("springjuergen", title = "JPA")
        val articles = service.allArticles("springjuergen")
        assertThat(articles).containsExactly(ArticleDTO("springjuergen", "Spring", "Lorem JPA"))
    }

    @Test
    fun comments() {
        service.delete("login42")

        val author = Author("login42", "Juergen", "Hoeller")
        author.addArticle(Article("Spring", "Dear Spring community ...", "Lorem ipsum", author))
        author.addComment(Comment("aaaaa", author))
        author.addComment(Comment("bbbbb", author))
        author.addComment(Comment("ccccc", author))
        service.save(author)

        val author42 = service.findAuthorByLogin("login42")
        assertThrows<LazyInitializationException> {
            println(author42?.getComments())
        }

        val author42withComments = service.findAuthorCommentsByLogin("login42")
        assertThat(author42withComments).containsExactly(CommentDTO("aaaaa"), CommentDTO("bbbbb"), CommentDTO("ccccc"))
    }

}