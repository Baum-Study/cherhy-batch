package com.example.batch.processer

import com.example.batch.model.InputModel
import com.example.batch.model.OutputModel
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