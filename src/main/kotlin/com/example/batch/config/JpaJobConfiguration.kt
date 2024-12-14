package com.example.batch.config

import com.example.batch.model.Customer
import jakarta.persistence.EntityManagerFactory
import mu.KotlinLogging
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private val logger = KotlinLogging.logger {}

@Configuration
class JpaJobConfiguration(
    private val entityManagerFactory: EntityManagerFactory,
) {
    @Bean
    fun customerJpaPagingItemReader() =
        JpaPagingItemReader<Customer>().apply {
            setQueryString("SELECT c FROM Customer c WHERE c.age > :age order by id desc")
            setEntityManagerFactory(entityManagerFactory)
            pageSize = CHUNK_SIZE
            val parameterMap = mapOf("age" to 20)
            setParameterValues(parameterMap)
        }

    @Bean
    fun jpaItemWriter() =
        JpaItemWriterBuilder<Customer>()
            .entityManagerFactory(entityManagerFactory)
            .usePersist(true)
            .build()

    companion object {
        const val CHUNK_SIZE = 100
    }
}

class CustomerItemProcessor : ItemProcessor<Customer, Customer> {
    override fun process(item: Customer): Customer {
        logger.info { "Item Processor ------------------- $item" }
        return item
    }
}