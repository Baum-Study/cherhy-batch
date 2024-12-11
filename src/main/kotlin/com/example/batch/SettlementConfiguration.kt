package com.example.batch

import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.batch.item.file.mapping.DefaultLineMapper
import org.springframework.batch.item.file.mapping.FieldSetMapper
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer
import org.springframework.batch.item.file.transform.FieldSet
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.transaction.PlatformTransactionManager
import java.time.LocalDateTime
import kotlin.text.Charsets.UTF_8

@Configuration
class SettlementConfiguration {
    @Bean
    fun settlementItemReader(): ItemReader<Payment> {
        val tokenizer =
            DelimitedLineTokenizer()
                .apply {
                    setNames("id", "sellerId", "productId", "productName", "price", "paymentDate")
                }

        val lineMapper =
            DefaultLineMapper<Payment>().apply {
                setLineTokenizer(tokenizer)
                setFieldSetMapper(PaymentFieldSetMapper())
            }

        return FlatFileItemReaderBuilder<Payment>()
            .name(SETTLEMENT_ITEM_READER)
            .resource(ClassPathResource("output/test-payments.csv"))
            .encoding(UTF_8.name())
            .lineMapper(lineMapper)
            .linesToSkip(1)
            .build()
    }

    @Bean
    fun settlementItemWriter(): ItemWriter<Payment> =
        ItemWriter { payments ->
            val settlements =
                payments.groupBy { it.sellerId }
                    .map { (sellerId, groupedPayments) ->
                        Settlement(
                            sellerId = sellerId,
                            amount = groupedPayments.sumOf { it.price },
                            settlementDate = LocalDateTime.now()
                        )
                    }

            val map = settlements.groupBy { it.sellerId }
            val result = map.sumOf { it.amount }

            println("result: $result")
            // TODO: Database 저장 로직 추가
        }

    @Bean
    fun settlementStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ) =
        StepBuilder(SETTLEMENT_STEP, jobRepository)
            .chunk<Payment, Payment>(1000, transactionManager)
            .reader(settlementItemReader())
            .writer(settlementItemWriter())
            .build()

    @Bean
    fun settlementJob(
        settlementStep: Step,
        jobRepository: JobRepository,
    ) =
        JobBuilder(SETTLEMENT_JOB, jobRepository)
            .incrementer(RunIdIncrementer())
            .start(settlementStep)
            .build()

    class PaymentFieldSetMapper : FieldSetMapper<Payment> {
        override fun mapFieldSet(
            fieldSet: FieldSet,
        ) =
            with(fieldSet) {
                Payment(
                    id = readLong("id"),
                    sellerId = readLong("sellerId"),
                    productId = readLong("productId"),
                    productName = readString("productName"),
                    price = readBigDecimal("price"),
                    paymentDate = LocalDateTime.parse(readString("paymentDate"))
                )
            }
    }

    companion object {
        const val SETTLEMENT_ITEM_READER = "settlementItemReader"
        const val SETTLEMENT_STEP = "settlementStep"
        const val SETTLEMENT_JOB = "settlementJob"
    }
}