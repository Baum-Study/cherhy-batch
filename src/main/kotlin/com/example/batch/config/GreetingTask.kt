package com.example.batch.config

import mu.KotlinLogging
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.InitializingBean

class GreetingTask : Tasklet, InitializingBean {
    private val logger = KotlinLogging.logger {}

    override fun execute(
        contribution: StepContribution,
        chunkContext: ChunkContext,
    ): RepeatStatus {
        logger.info { "------------------ Task Execute -----------------" }
        logger.info { "GreetingTask : $contribution, $chunkContext" }
        return RepeatStatus.FINISHED
    }

    override fun afterPropertiesSet() {
        logger.info { "----------------- After Properties Set --------------" }
    }
}