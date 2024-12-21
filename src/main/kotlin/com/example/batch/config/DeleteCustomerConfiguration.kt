package com.example.batch.config

import com.example.batch.model.Image
import com.example.batch.model.Status
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.database.JpaCursorItemReader
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class DeleteCustomerConfiguration(
    private val entityManagerFactory: EntityManagerFactory,
) {
    /**
     * fetch join을 사용하지 않았을 때는 customer를 조회할 때 query가 계속 나가서 이미지 수 만큼 DB에 쿼리를 날린다.
     */
    @Bean(DELETE_CUSTOMER_READER)
    fun deleteCustomerReader() =
        JpaCursorItemReader<Image>().apply {
            setQueryString(
                """
                    SELECT image 
                    FROM Image image 
                    JOIN FETCH image.customer customer 
                    WHERE customer.status = :status
                    ORDER BY image.id
                """
            )

            setEntityManagerFactory(entityManagerFactory)
            val parameterMap = mapOf("status" to Status.INACTIVE)
            setParameterValues(parameterMap)
        }

    @Bean(DELETE_CUSTOMER_PROCESSOR)
    fun deleteCustomerProcessor() =
        ItemProcessor<Image, Image> { image ->
            entityManagerFactory.createEntityManager().apply {
                transaction.begin()

                val managedImage = merge(image)
                val managedCustomer = merge(image.customer)

                remove(managedImage)
                remove(managedCustomer)

                transaction.commit()
                close()
            }
            null
        }

    @Bean(DELETE_CUSTOMER_WRITER)
    fun deleteCustomerWriter() =
        JpaItemWriter<Image>().apply {
            setEntityManagerFactory(entityManagerFactory)
            setUsePersist(false)
            afterPropertiesSet()
        }

    @Bean(DELETE_CUSTOMER_STEP)
    fun deleteCustomerStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ) =
        StepBuilder(DELETE_CUSTOMER_STEP, jobRepository)
            .chunk<Image, Image>(CHUNK_SIZE, transactionManager)
            .reader(deleteCustomerReader())
            .processor(deleteCustomerProcessor())
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
        const val DELETE_CUSTOMER_PROCESSOR = "deleteCustomerProcessor"
        const val DELETE_CUSTOMER_WRITER = "deleteCustomerWriter"
        const val DELETE_CUSTOMER_STEP = "deleteCustomerStep"
        const val DELETE_CUSTOMER_JOB = "deleteCustomerJob"
    }
}