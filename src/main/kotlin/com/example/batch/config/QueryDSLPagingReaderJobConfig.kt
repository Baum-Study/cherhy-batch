package com.example.batch.config

import com.example.batch.model.Customer
import com.example.batch.model.QCustomer.customer
import com.example.batch.processer.CustomItemWriter
import com.example.batch.processer.CustomService
import jakarta.persistence.EntityManagerFactory
import mu.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.FileSystemResource
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class QueryDSLPagingReaderJobConfig(
    private val entityManagerFactory: EntityManagerFactory,
    private val customService: CustomService,
) {
    private val logger = KotlinLogging.logger {}

    @Bean(QUERYDSL_PAGING_CHUNK_ITEM_READER)
    fun customerQuerydslPagingItemReader() =
        QuerydslPagingItemReader<Customer>(
            name = QUERYDSL_PAGING_CHUNK_ITEM_READER,
            chunkSize = CHUNK_SIZE,
            entityManagerFactory = entityManagerFactory,
        ) {
            it.select(customer).from(customer)
                .where(
                    customer.age.gt(20)
                )
        }

    @Bean(QUERYDSL_PAGING_FLAT_FILE_ITEM_WRITER)
    fun customerQuerydslFlatFileItemWriter() =
        FlatFileItemWriterBuilder<Customer>()
            .name(QUERYDSL_PAGING_FLAT_FILE_ITEM_WRITER)
            .resource(FileSystemResource("./output/customer_new_v2.csv"))
            .encoding(ENCODING)
            .delimited().delimiter("\t")
            .names("Name", "Age", "Gender")
            .build()

    @Bean(CUSTOM_ITEM_WRITER)
    fun customItemWriter() =
        CustomItemWriter(customService)

    @Bean(QUERYDSL_PAGING_CHUNK_STEP)
    fun customerQuerydslPagingStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ): Step {
        logger.info { "------------------ Init customerQuerydslPagingStep -----------------" }

        return StepBuilder(QUERYDSL_PAGING_CHUNK_STEP, jobRepository)
            .chunk<Customer, Customer>(CHUNK_SIZE, transactionManager)
            .reader(customerQuerydslPagingItemReader())
            .processor(CustomerItemProcessor())
            .writer(customerQuerydslFlatFileItemWriter())
            .writer(customItemWriter())
            .build()
    }

    @Bean(QUERYDSL_PAGING_CHUNK_JOB)
    fun customerJpaPagingJob(
        customerQuerydslPagingStep: Step,
        jobRepository: JobRepository,
    ): Job {
        logger.info { "------------------ Init customerJpaPagingJob -----------------" }

        return JobBuilder(QUERYDSL_PAGING_CHUNK_JOB, jobRepository)
            .incrementer(RunIdIncrementer())
            .start(customerQuerydslPagingStep)
            .build()
    }

    private companion object {
        const val CHUNK_SIZE = 2
        const val ENCODING = "UTF-8"
        const val QUERYDSL_PAGING_CHUNK_ITEM_READER = "customerQuerydslPagingItemReader"
        const val QUERYDSL_PAGING_FLAT_FILE_ITEM_WRITER = "customerQuerydslFlatFileItemWriter"
        const val CUSTOM_ITEM_WRITER = "customItemWriter"
        const val QUERYDSL_PAGING_CHUNK_STEP = "customerQuerydslPagingStep"
        const val QUERYDSL_PAGING_CHUNK_JOB = "customerQuerydslPagingJob"
    }
}