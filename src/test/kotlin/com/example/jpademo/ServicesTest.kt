package com.example.jpademo

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ServicesTest @Autowired constructor(
    val service: Service
) {

    @Test
    fun `When findByLogin then article can be updated`() {
        val juergen = Author("springjuergen", "Juergen", "Hoeller")
        val article = Article("Spring Framework 5.0 goes GA", "Dear Spring community ...", "Lorem ipsum", juergen)
        juergen.articles.add(article)
        service.save(juergen)
        service.updateContent()
        val allArticles = service.allArticles("springjuergen")
        println(allArticles)
    }
}