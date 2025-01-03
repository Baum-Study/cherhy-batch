package com.example.batch.config

import com.example.batch.model.PurchasedProduct
import org.apache.ibatis.session.SqlSessionFactory
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

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
            .writer(completeProductItemWriter())
            .build()

    @Bean(COMPLETE_PRODUCT_ITEM_READER)
    fun completeProductItemReader() =
        MyBatisPagingItemReaderBuilder<PurchasedProduct>()
            .pageSize(1000)
            .sqlSessionFactory(sqlSessionFactory)
            .queryId(PRODUCT_READ_QUERY)
            .build()!!

    @Bean(COMPLETE_PRODUCT_ITEM_WRITER)
    fun completeProductItemWriter() =
        MyBatisBatchItemWriterBuilder<PurchasedProduct>()
            .sqlSessionFactory(sqlSessionFactory)
            .statementId(PRODUCT_WRITE_QUERY)
            .build()!!

    private companion object {
        const val PRODUCT_READ_QUERY = "com.example.batch.mapper.ProductMapper.selectProducts"
        const val PRODUCT_WRITE_QUERY = "com.example.batch.mapper.ProductMapper.updateProducts"
        const val COMPLETE_PRODUCT_JOB = "completeProductJob"
        const val COMPLETE_PRODUCT_STEP = "completeProductStep"
        const val COMPLETE_PRODUCT_ITEM_READER = "completeProductItemReader"
        const val COMPLETE_PRODUCT_ITEM_WRITER = "completeProductItemWriter"
    }
}