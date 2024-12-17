package com.example.batch

import com.example.batch.config.DeleteCustomerConfiguration.Companion.DELETE_CUSTOMER_JOB
import com.example.batch.config.SettlementConfiguration.Companion.SETTLEMENT_JOB
import com.example.batch.lib.JobParameterFactory
import io.kotest.core.spec.style.StringSpec
import org.springframework.batch.core.Job
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.JobRepositoryTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate

// 참고 했던 블로그 : https://jojoldu.tistory.com/551
@SpringBootTest
@SpringBatchTest
class CreateCustomerAndImageTests(
    @Autowired private val jobLauncherTestUtils: JobLauncherTestUtils,
    @Autowired private val jobRepositoryTestUtils: JobRepositoryTestUtils,
    @Autowired @Qualifier(DELETE_CUSTOMER_JOB) private val settlementJob: Job,
    @Autowired private val jdbcTemplate: JdbcTemplate,
) : StringSpec({
    afterEach {
        jobRepositoryTestUtils.removeJobExecutions()
    }

    "데이터베이스에 회원 10만명과 이미지 100만개를 생성한다." {
        val customerCount = 100_000
        val imageCount = 1_000_000

    }

    "" {
        jobLauncherTestUtils.job = settlementJob
        val job = JobParameterFactory.create(SETTLEMENT_JOB)

        jobLauncherTestUtils.launchJob(job)

    }
})