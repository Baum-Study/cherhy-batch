package com.example.batch.config

import mu.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class BasicTaskJobConfig(
    private val transactionManager: PlatformTransactionManager,
) {
    private val logger = KotlinLogging.logger {}

    @Bean
    fun greetingTasklet(): Tasklet =
        GreetingTask()

    @Bean
    fun step(
        jobRepository: JobRepository,
    ): Step {
        logger.info { "------------------ Init myStep -----------------" }
        return StepBuilder("myStep", jobRepository)
            .tasklet(greetingTasklet(), transactionManager)
            .build()
    }

    @Bean
    fun myJob(
        step: Step,
        jobRepository: JobRepository,
    ): Job {
        logger.info { "------------------ Init myJob -----------------" }
        return JobBuilder("myJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(step)
            .build()
    }
}