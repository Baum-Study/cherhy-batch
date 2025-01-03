package com.example.batch

import com.example.batch.config.ProductRepository
import io.kotest.core.spec.style.BehaviorSpec
import org.springframework.batch.core.Job
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.JobRepositoryTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.support.TransactionTemplate

@SpringBootTest
@SpringBatchTest
class CompleteProductJobTest(
    private val jobLauncherTestUtils: JobLauncherTestUtils,
    private val jobRepositoryTestUtils: JobRepositoryTestUtils,
    private val completeProductJob: Job,
    private val productRepository: ProductRepository,
    private val transactionTemplate: TransactionTemplate,
): BehaviorSpec({

})