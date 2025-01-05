package com.example.batch.config

import com.example.batch.model.PurchaseStatus
import com.example.batch.model.PurchasedProduct
import com.example.batch.util.extension.sumOf
import org.apache.ibatis.session.SqlSessionFactory
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.support.CompositeItemWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import java.time.LocalDateTime

@Configuration
class CompleteProductJobConfig(
    private val sqlSessionFactory: SqlSessionFactory,
) {

    @Bean(COMPLETE_PRODUCT_JOB)
    fun completeProductJob(
        completeProductStep: Step,
        jobRepository: JobRepository,
    ) =
        JobBuilder(COMPLETE_PRODUCT_JOB, jobRepository)
            .start(completeProductStep)
            .build()

    @Bean(COMPLETE_PRODUCT_STEP)
    fun completeProductStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ) =
        StepBuilder(COMPLETE_PRODUCT_STEP, jobRepository)
            .chunk<PurchasedProduct, PurchasedProduct>(1000, transactionManager)
            .reader(completeProductItemReader())
            .writer(compositeItemWriter())
            .build()

    @Bean(COMPLETE_PRODUCT_ITEM_READER)
    fun completeProductItemReader() =
        MyBatisPagingItemReaderBuilder<PurchasedProduct>()
            .pageSize(1000)
            .sqlSessionFactory(sqlSessionFactory)
            .queryId(PRODUCT_READ_QUERY)
            .parameterValues(
                mapOf(
                    "status" to PurchaseStatus.PURCHASED,
                    "date" to LocalDateTime.now().minusWeeks(1),
                )
            )
            .build()!!

    @Bean(COMPOSITE_ITEM_WRITER)
    fun compositeItemWriter() =
        CompositeItemWriter<PurchasedProduct>().apply {
            setDelegates(
                listOf(
                    completeProductItemWriter(),
                    addMileageWriter(),
                )
            )
        }

    @Bean(COMPLETE_PRODUCT_ITEM_WRITER)
    fun completeProductItemWriter() =
        MyBatisBatchItemWriterBuilder<PurchasedProduct>()
            .sqlSessionFactory(sqlSessionFactory)
            .statementId(PRODUCT_WRITE_QUERY)
            .build()!!

    @Bean
    fun addMileageWriter(): ItemWriter<PurchasedProduct> =
        ItemWriter { chunk: Chunk<out PurchasedProduct> ->
            val mileageMap =
                chunk.groupBy { it.customerId }
                    .sumOf { it.price * 0.01.toBigDecimal() }

            sqlSessionFactory.openSession().use { session ->
                mileageMap.forEach { (customerId, price) ->
                    session.update(
                        ADD_MILEAGE_QUERY,
                        mapOf(
                            "customerId" to customerId,
                            "price" to price,
                        )
                    )
                }
            }
        }

    companion object {
        const val COMPOSITE_ITEM_WRITER = "compositeItemWriter"
        const val PRODUCT_READ_QUERY = "com.example.batch.config.PurchaseRepository.findAll"
        const val PRODUCT_WRITE_QUERY = "com.example.batch.config.PurchaseRepository.updateDeliveryComplete"
        const val ADD_MILEAGE_QUERY = "com.example.batch.config.MileageRepository.update"
        const val COMPLETE_PRODUCT_JOB = "completeProductJob"
        const val COMPLETE_PRODUCT_STEP = "completeProductStep"
        const val COMPLETE_PRODUCT_ITEM_READER = "completeProductItemReader"
        const val COMPLETE_PRODUCT_ITEM_WRITER = "completeProductItemWriter"
    }
}