create or alter view author_view as
    select a.id, a.login, max(c.added_at) as lastmod
    from author a
    join comment c on a.id = c.author_id
    group by a.id, a.login
go
