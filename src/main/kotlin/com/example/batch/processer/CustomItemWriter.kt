package com.example.batch.processer

import com.example.batch.model.Customer
import mu.KotlinLogging
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Component
class CustomItemWriter(
    private val customService: CustomService,
) : ItemWriter<Customer> {
    private val logger = KotlinLogging.logger {}

    override fun write(
        chunk: Chunk<out Customer>,
    ) {
        for (customer in chunk) {
            logger.info { "Call Process in CustomItemWriter..." }
            customService.processToOtherService(customer)
        }
    }
}

@Service
class CustomService {
    private val logger = KotlinLogging.logger {}

    fun processToOtherService(
        customer: Customer,
    ) {
        logger.info { "Call processToOtherService in CustomService..." }
    }
}