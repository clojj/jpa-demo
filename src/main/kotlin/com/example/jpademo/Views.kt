package com.example.jpademo

import org.hibernate.annotations.Immutable
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

// https://medium.com/@jonathan.turnock/exposing-subset-view-of-the-database-with-a-jpa-repository-over-rest-5b9d6e07344b

@Entity
@Immutable
@Table(name = "author_view")
class AuthorView(
    @Column
    var login: String,

    @Column
    var lastmod: LocalDateTime,

    @Id
    var id: Long? = null
)