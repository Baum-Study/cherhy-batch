package com.example.batch.config

import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.item.database.AbstractPagingItemReader

class QuerydslPagingItemReader<T>(
    name: String,
    chunkSize: Int,
    private val entityManagerFactory: EntityManagerFactory,
    private val querySupplier: (JPAQueryFactory) -> JPAQuery<T>,
) : AbstractPagingItemReader<T>() {
    init {
        setName(name)
        pageSize = chunkSize
    }

    override fun doClose() {
        entityManagerFactory.close()
        super.doClose()
    }

    override fun doReadPage() {
        if (results.isEmpty()) results = emptyList()
        else results.clear()

        val entityManager = entityManagerFactory.createEntityManager()

        val jpaQueryFactory = JPAQueryFactory(entityManager)
        val offset = page.toLong() * pageSize

        val query = querySupplier.invoke(jpaQueryFactory).offset(offset).limit(pageSize.toLong())
        val queryResult = query.fetch()
        for (entity in queryResult) {
            entityManager.detach(entity)
            results.add(entity)
        }
    }
}