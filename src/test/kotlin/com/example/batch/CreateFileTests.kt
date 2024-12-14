package com.example.batch

import com.example.batch.SettlementConfiguration.Companion.SETTLEMENT_JOB
import com.example.batch.lib.JobParameterFactory
import com.example.batch.lib.PaymentFactory
import com.example.batch.lib.mapParallel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.batch.core.Job
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.JobRepositoryTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.core.JdbcTemplate
import java.io.File
import java.io.PrintWriter

// 참고 했던 블로그 : https://jojoldu.tistory.com/525
@SpringBootTest
@SpringBatchTest
class CreateFileTests(
    @Autowired private val jobLauncherTestUtils: JobLauncherTestUtils,
    @Autowired private val jobRepositoryTestUtils: JobRepositoryTestUtils,
    @Autowired @Qualifier(SETTLEMENT_JOB) private val settlementJob: Job,
    @Autowired private val jdbcTemplate: JdbcTemplate,
) : StringSpec({
    afterEach {
        jobRepositoryTestUtils.removeJobExecutions()
    }

    "랜덤 데이터를 생성해서 csv 파일을 만든다." {
        val payments = 10_000.mapParallel(PaymentFactory::generateRandom)
        val reader = LinkedListItemReader(payments)

        val resource = ClassPathResource("output/test-payments.csv")
        val file = File(resource.uri)
        file.parentFile.mkdirs()

        PrintWriter(file).use { writer ->
            writer.println("id,sellerId,productId,productName,price,paymentDate")

            var payment = reader.read()

            while (payment != null) {
                writer.println("${payment!!.id},${payment!!.sellerId},${payment!!.productId},${payment!!.productName},${payment!!.price},${payment!!.paymentDate}")
                payment = reader.read()
            }
        }
    }

    "csv 파일로 만든 결제 데이터를 읽어서 정산한다." {
        jobLauncherTestUtils.job = settlementJob
        val job = JobParameterFactory.create(SETTLEMENT_JOB)

        jobLauncherTestUtils.launchJob(job)
        val settlements =
            jdbcTemplate.query("SELECT * FROM settlement") { rs, _ ->
                Settlement(
                    sellerId = rs.getLong("seller_id"),
                    amount = rs.getBigDecimal("amount"),
                    settlementDate = rs.getTimestamp("settlement_date").toLocalDateTime()
                )
            }

        val sortedSettlements = settlements.sortedBy { it.sellerId }

        sortedSettlements.forEach {
            println(it)
        }

        sortedSettlements.size shouldBe 100
    }
})