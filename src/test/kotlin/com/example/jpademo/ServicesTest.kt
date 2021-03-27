package com.example.jpademo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
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
        assertThat(allArticles).containsExactly(ArticleDTO("springjuergen", "Spring", "Lorem JPA"), ArticleDTO("springjuergen", "JPA", "Lorem JPA"))

        service.deleteArticle("springjuergen", title = "JPA")
        val articles = service.allArticles("springjuergen")
        assertThat(articles).containsExactly(ArticleDTO("springjuergen", "Spring", "Lorem JPA"))
    }

}