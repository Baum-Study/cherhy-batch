package com.example.batch

import mu.KotlinLogging
import org.springframework.batch.item.ItemProcessor

class MyItemProcessor: ItemProcessor<InputModel, OutputModel> {
    private val logger = KotlinLogging.logger {}

    override fun process(
        item: InputModel
    ): OutputModel {
        logger.info { "Processing item: $item" }

        return OutputModel(
            id = item.id,
            name = item.name,
        )
    }
}

data class InputModel(
    val id: Int,
    val name: String,
)

data class OutputModel(
    val id: Int,
    val name: String,
)