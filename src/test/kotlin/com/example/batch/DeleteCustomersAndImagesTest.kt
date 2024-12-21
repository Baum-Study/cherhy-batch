package com.example.batch

import com.example.batch.config.CustomerRepository
import com.example.batch.config.DeleteCustomerConfiguration.Companion.DELETE_CUSTOMER_JOB
import com.example.batch.config.ImageRepository
import com.example.batch.lib.CustomerFactory
import com.example.batch.lib.ImageFactory
import com.example.batch.lib.JobParameterFactory
import com.example.batch.lib.mapParallel
import com.example.batch.model.Status
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.JobRepositoryTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import kotlin.time.measureTime

// 참고 했던 블로그 : https://jojoldu.tistory.com/551
@SpringBootTest
@SpringBatchTest
class DeleteCustomersAndImagesTest(
    @Autowired private val jobLauncherTestUtils: JobLauncherTestUtils,
    @Autowired private val jobRepositoryTestUtils: JobRepositoryTestUtils,
    @Autowired @Qualifier(DELETE_CUSTOMER_JOB) private val deleteCustomerJob: Job,
    @Autowired private val customerRepository: CustomerRepository,
    @Autowired private val imageRepository: ImageRepository,
    @Autowired private val transactionTemplate: TransactionTemplate,
) : BehaviorSpec({
    afterTest {
        jobRepositoryTestUtils.removeJobExecutions()
    }

    Given("데이터베이스에 회원 10만명과 이미지 100만개를 생성한다.") {
        val customerCount = 100_000
        val imageCount = 1_000_000

        measureTime {
            val randomCustomers = customerCount.mapParallel(CustomerFactory::generateRandomV2)
            val randomImages =
                imageCount.mapParallel {
                    ImageFactory.generateRandomV2(randomCustomers.random())
                }

            transactionTemplate.executeWithoutResult {
                customerRepository.saveAll(randomCustomers)
                imageRepository.saveAll(randomImages)
            }
        }.let {
            println("데이터 생성 소요 시간: $it")
        }

        val activeCustomerSize = customerRepository.countByStatus(Status.ACTIVE)
        val activeImageSize =
            transactionTemplate.writeTransaction {
                val activeCustomers = customerRepository.findByStatus(Status.ACTIVE)
                imageRepository.countByCustomerIn(activeCustomers)
            }

        When("비활성화된 회원을 삭제하고 연결된 이미지도 삭제한다.") {
            measureTime {
                jobLauncherTestUtils.job = deleteCustomerJob
                val job = JobParameterFactory.create(DELETE_CUSTOMER_JOB)

                jobLauncherTestUtils.launchJob(job)
            }.let {
                println("배치 작업 총 소요 시간: $it")
            }
            Then("삭제된 회원과 이미지의 수만큼 데이터베이스의 회원과 이미지 수가 감소한다.") {
                customerRepository.count() shouldBe activeCustomerSize
                imageRepository.count() shouldBe activeImageSize
            }
        }
    }
}) {
    @TestConfiguration
    class TestConfig {
        @Bean(name = ["testTransactionManager"])
        @Primary
        fun testTransactionManager(
            entityManagerFactory: EntityManagerFactory,
        ) =
            JpaTransactionManager().apply {
                this.entityManagerFactory = entityManagerFactory
            }
    }
}

private fun <T> TransactionTemplate.writeTransaction(
    block: () -> T,
): T =
    execute { block() }!!