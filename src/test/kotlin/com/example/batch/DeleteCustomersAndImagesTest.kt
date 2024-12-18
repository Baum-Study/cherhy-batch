package com.example.batch

import com.example.batch.config.CustomerRepository
import com.example.batch.config.DeleteCustomerConfiguration.Companion.DELETE_CUSTOMER_JOB
import com.example.batch.config.ImageRepository
import com.example.batch.config.SettlementConfiguration.Companion.SETTLEMENT_JOB
import com.example.batch.lib.CustomerFactory
import com.example.batch.lib.ImageFactory
import com.example.batch.lib.JobParameterFactory
import com.example.batch.lib.mapParallel
import com.example.batch.model.Status
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.batch.core.Job
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.JobRepositoryTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest

// 참고 했던 블로그 : https://jojoldu.tistory.com/551
@SpringBootTest
@SpringBatchTest
class DeleteCustomersAndImagesTest(
    @Autowired private val jobLauncherTestUtils: JobLauncherTestUtils,
    @Autowired private val jobRepositoryTestUtils: JobRepositoryTestUtils,
    @Autowired @Qualifier(DELETE_CUSTOMER_JOB) private val settlementJob: Job,
    @Autowired private val customerRepository: CustomerRepository,
    @Autowired private val imageRepository: ImageRepository,
) : BehaviorSpec({
    afterEach {
        jobRepositoryTestUtils.removeJobExecutions()
    }

    Given("데이터베이스에 회원 10만명과 이미지 100만개를 생성한다.") {
        val customerCount = 100_000
        val imageCount = 1_000_000

        val randomCustomers =
            customerCount.mapParallel(CustomerFactory::generateRandom)

        val randomImages =
            imageCount.mapParallel {
                ImageFactory.generateRandom(randomCustomers.random())
            }

        customerRepository.saveAll(randomCustomers)
        imageRepository.saveAll(randomImages)

        val allCustomerSize = customerRepository.count()
        val allImageSize = imageRepository.count()

        val deleteCustomers = customerRepository.findByStatus(Status.ACTIVE)
        val deleteCustomerSize = deleteCustomers.size

        val deleteImages = imageRepository.findByCustomerIn(deleteCustomers)
        val deleteImageSize = deleteImages.size

        When("비활성화된 회원을 삭제하고 연결된 이미지도 삭제한다.") {
            jobLauncherTestUtils.job = settlementJob
            val job = JobParameterFactory.create(SETTLEMENT_JOB)

            jobLauncherTestUtils.launchJob(job)

            Then("삭제된 회원과 이미지의 수만큼 데이터베이스의 회원과 이미지 수가 감소한다.") {
                val activeCustomerSize = customerRepository.countByStatus(Status.ACTIVE)
                activeCustomerSize shouldBe allCustomerSize - deleteCustomerSize

                val activeImageSize = imageRepository.count()
                activeImageSize shouldBe allImageSize - deleteImageSize
            }
        }
    }
})