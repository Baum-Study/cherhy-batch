package com.example.batch.config

import mu.KotlinLogging
import org.springframework.batch.core.*
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager


@Configuration
class JobListenerConfig {
    val logger = KotlinLogging.logger {}

    @Bean
    fun jobExecutionListener() =
        object : JobExecutionListener {
            override fun beforeJob(
                jobExecution: JobExecution,
            ) {
                logger.info { " >>>>>> Before job: Job ${jobExecution.jobInstance.jobName} is starting..." }
            }

            override fun afterJob(
                jobExecution: JobExecution,
            ) {
                logger.info { " >>>>>> After job: Job ${jobExecution.jobInstance.jobName} is finished." }
            }
        }

    @Bean
    fun nextStepJob(
        step01: Step,
        step02: Step,
        jobRepository: JobRepository,
        jobExecutionListener: JobExecutionListener
    ) =
        JobBuilder(NEXT_STEP_TASK, jobRepository)
            .incrementer(RunIdIncrementer())
            .start(step01)
            .next(step02)
            .listener(jobExecutionListener)
            .build()

    companion object {
        const val NEXT_STEP_TASK = "nextStepTask"
    }
}

@Configuration
class StepListenerConfig {
    val logger = KotlinLogging.logger {}

    @Bean(STEP_EXECUTION_LISTENER)
    fun stepExecutionListener() =
        object : StepExecutionListener {
            override fun beforeStep(
                stepExecution: StepExecution,
            ) {
                logger.info { " >>>>>> Before step: Step ${stepExecution.stepName} is starting..." }
            }

            override fun afterStep(
                stepExecution: StepExecution,
            ): ExitStatus {
                logger.info { " >>>>>> After step: Step ${stepExecution.stepName} is finished." }
                return stepExecution.exitStatus
            }
        }

    @Bean(STEP_01)
    fun step01(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        stepExecutionListener: StepExecutionListener,
    ) =
        StepBuilder(STEP_01, jobRepository)
            .tasklet({ _, _ -> RepeatStatus.FINISHED }, transactionManager)
            .listener(stepExecutionListener)
            .build()

    @Bean(STEP_02)
    fun step02(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        stepExecutionListener: StepExecutionListener,
    ) =
        StepBuilder(STEP_02, jobRepository)
            .tasklet({ _, _ -> RepeatStatus.FINISHED }, transactionManager)
            .listener(stepExecutionListener)
            .build()

    companion object {
        const val STEP_EXECUTION_LISTENER = "stepExecutionListener"
        const val STEP_01 = "step01"
        const val STEP_02 = "step02"
    }
}
