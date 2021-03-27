package com.example.jpademo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ServicesTest @Autowired constructor(
    val service: Service
) {

    @Test
    @Order(0)
    fun init() {
        service.delete("springjuergen")
    }

    @Test
    @Order(1)
    fun `author with article can be created`() {
        val juergen = Author("springjuergen", "Juergen", "Hoeller")
        val article = Article("Spring Framework 5.0 goes GA", "Dear Spring community ...", "Lorem ipsum", juergen)
        juergen.articles.add(article)
        service.save(juergen)
    }

    @Test
    @Order(2)
    fun `article can be updated`() {
        service.updateContent()
    }

    @Test
    @Order(3)
    fun `updated article can be found`() {
        val allArticles = service.allArticles("springjuergen")
        assertThat(allArticles).containsExactly(ArticleDTO("springjuergen", "Kotlin JPA!"))
    }
}