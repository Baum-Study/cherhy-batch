package com.example.batch.config

import com.example.batch.model.Customer
import org.springframework.batch.item.ItemProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ItemProcessorConfig {
    @Bean
    fun lowerCaseItemProcessor() = LowerCaseItemProcessor()

    @Bean
    fun after20YearsItemProcessor() = After20YearsItemProcessor()
}

class LowerCaseItemProcessor : ItemProcessor<Customer, Customer> {
    override fun process(
        item: Customer,
    ) =
        Customer(
            id = item.id,
            name = item.name.lowercase(),
            age = item.age,
            status = item.status,
        )
}

class After20YearsItemProcessor : ItemProcessor<Customer, Customer> {
    override fun process(
        item: Customer,
    ) =
        Customer(
            id = item.id,
            name = item.name,
            age = item.age + 20,
            status = item.status,
        )
}