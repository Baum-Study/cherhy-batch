package com.example.batch.config

import com.example.batch.model.Customer
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CompositeItemProcessorConfig(
    private val lowerCaseItemProcessor: LowerCaseItemProcessor,
    private val after20YearsItemProcessor: After20YearsItemProcessor,
) {
    @Bean
    fun compositeItemProcessor() =
        CompositeItemProcessorBuilder<Customer, Customer>()
            .delegates(lowerCaseItemProcessor, after20YearsItemProcessor)
            .build()
}