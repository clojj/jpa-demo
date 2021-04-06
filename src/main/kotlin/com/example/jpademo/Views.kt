package com.example.jpademo

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.Immutable
import java.time.LocalDateTime
import javax.persistence.*

// https://medium.com/@jonathan.turnock/exposing-subset-view-of-the-database-with-a-jpa-repository-over-rest-5b9d6e07344b

@Entity
@Immutable
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "author_view")
class AuthorView(
    @Column
    var login: String,

    @Column
    var lastmod: LocalDateTime,

    @Id
    var id: Long? = null
)