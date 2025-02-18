package com.example.batch

import com.example.batch.config.CompleteProductJobConfig.Companion.COMPLETE_PRODUCT_JOB
import com.example.batch.config.CustomerRepository
import com.example.batch.config.MileageRepository
import com.example.batch.config.ProductRepository
import com.example.batch.config.PurchaseRepository
import com.example.batch.lib.*
import com.example.batch.model.PurchaseStatus
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.bigdecimal.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import org.springframework.batch.core.Job
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.JobRepositoryTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBootTest
@SpringBatchTest
class CompleteProductJobTest(
    private val completeProductJob: Job,
    private val productRepository: ProductRepository,
    private val purchaseRepository: PurchaseRepository,
    private val customerRepository: CustomerRepository,
    private val jobLauncherTestUtils: JobLauncherTestUtils,
    private val jobRepositoryTestUtils: JobRepositoryTestUtils,
    private val mileageRepository: MileageRepository,
) : BehaviorSpec({
    afterTest {
        jobRepositoryTestUtils.removeJobExecutions()
    }

    Given("데이터베이스에 회원 10만명과 상품 100만개, 구매 정보 100만개를 생성한다.") {
        val customerCount = 100_000
        val productCount = 1_000_000
        val oneWeekAgo = LocalDateTime.now().minusWeeks(1)

        val randomCustomers = customerCount.mapParallel(CustomerFactory::generateRandomV2)
        val savedCustomerIds = customerRepository.saveAll(randomCustomers).map { it.id }

        val randomProducts =
            productCount.mapParallel {
                ProductFactory.generateRandomV2(savedCustomerIds.random())
            }

        val savedProductCount = productRepository.saveAll(randomProducts)

        val randomPurchasedProducts =
            productCount.mapParallel {
                PurchaseFactory.generateRandomV2(savedCustomerIds.random(), randomProducts.random())
            }

        val saveSucceedPurchasedCount = purchaseRepository.saveAll(randomPurchasedProducts)
        val deliveryCompletedProductCount = randomPurchasedProducts.filter { it.purchasedAt < oneWeekAgo }.size

        val randomMileages =
            customerCount.mapParallel {
                MileageFactory.generateRandomV2(savedCustomerIds.random())
            }

        val savedMileages = mileageRepository.saveAll(randomMileages)

        and("회원 10만명, 상품 100만개, 구매 정보 100만개, 마일리지 기본 값 10만개가 생성되었다.") {
            savedCustomerIds.size shouldBe customerCount
            savedProductCount shouldBe productCount
            saveSucceedPurchasedCount shouldBe productCount
            savedMileages shouldBe customerCount

            When("구매일자가 일주일이 지난 상품은 배달 완료로 처리한다.") {
                jobLauncherTestUtils.job = completeProductJob
                val job = JobParameterFactory.create(COMPLETE_PRODUCT_JOB)

                jobLauncherTestUtils.launchJob(job)

                Then("배달 완료 처리된 상품은 1주일이 지난 상품의 개수와 같아야 한다.") {
                    val result =
                        purchaseRepository.findAll(PurchaseStatus.DELIVERED, LocalDateTime.now().minusWeeks(1)).size

                    deliveryCompletedProductCount shouldBe result
                }

                Then("배달 완료가 되었다면 마일리지가 쌓여야 한다.") {
                    val mileages = mileageRepository.findAll()

                    mileages.forEach {mileage ->
                        val originalMileage = randomMileages.first { it.customerId == mileage.customerId }
                        mileage.price shouldBeGreaterThan originalMileage.price
                    }
                }
            }
        }
    }
})