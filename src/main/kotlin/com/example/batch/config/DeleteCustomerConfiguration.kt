package com.example.batch.config

import com.example.batch.model.Customer
import com.example.batch.model.Status
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.database.JpaCursorItemReader
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class DeleteCustomerConfiguration(
    private val entityManagerFactory: EntityManagerFactory,
) {
    @Bean(DELETE_CUSTOMER_READER)
    fun deleteCustomerReader() =
        JpaCursorItemReader<Customer>().apply {
            setQueryString("SELECT c FROM Customer c WHERE c.status = :status order by id desc")
            setEntityManagerFactory(entityManagerFactory)
            val parameterMap = mapOf("status" to Status.INACTIVE.name)
            setParameterValues(parameterMap)
        }

    @Bean(DELETE_CUSTOMER_WRITER)
    fun deleteCustomerWriter() =
        JpaItemWriterBuilder<Customer>()
            .entityManagerFactory(entityManagerFactory)
            .usePersist(true)
            .build()

    @Bean(DELETE_CUSTOMER_STEP)
    fun deleteCustomerStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ) =
        StepBuilder(DELETE_CUSTOMER_STEP, jobRepository)
            .chunk<Customer, Customer>(CHUNK_SIZE, transactionManager)
            .reader(deleteCustomerReader())
            .writer(deleteCustomerWriter())
            .build()

    @Bean(DELETE_CUSTOMER_JOB)
    fun deleteCustomerJob(
        jobRepository: JobRepository,
        deleteCustomerStep: Step,
    ) =
        JobBuilder(DELETE_CUSTOMER_JOB, jobRepository)
            .incrementer(RunIdIncrementer())
            .start(deleteCustomerStep)
            .build()

    companion object {
        private const val CHUNK_SIZE = 1000
        const val DELETE_CUSTOMER_READER = "deleteCustomerReader"
        const val DELETE_CUSTOMER_WRITER = "deleteCustomerWriter"
        const val DELETE_CUSTOMER_STEP = "deleteCustomerStep"
        const val DELETE_CUSTOMER_JOB = "deleteCustomerJob"
    }
}