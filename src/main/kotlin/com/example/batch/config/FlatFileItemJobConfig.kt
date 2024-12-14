package com.example.batch.config

import com.example.batch.model.Customer
import com.example.batch.processer.AggregateCustomerProcessor
import com.example.batch.processer.CustomerFooter
import com.example.batch.processer.CustomerHeader
import com.example.batch.processer.CustomerLineAggregator
import mu.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.FlatFileItemWriter
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.transaction.PlatformTransactionManager
import java.util.concurrent.ConcurrentHashMap
import javax.xml.transform.OutputKeys

@Configuration
class FlatFileItemJobConfig {
    private val logger = KotlinLogging.logger {}
    private val aggregateInfos = ConcurrentHashMap<String, Int>()
    private val itemProcessor = AggregateCustomerProcessor(aggregateInfos)

    @Bean
    fun flatFileItemReader(): FlatFileItemReader<Customer> {
        return FlatFileItemReaderBuilder<Customer>()
            .name("FlatFileItemReader")
            .resource(ClassPathResource("./customer.csv"))
            .encoding(OutputKeys.ENCODING)
            .delimited().delimiter(",")
            .names("name", "age", "gender")
            .targetType(Customer::class.java)
            .build()
    }

    @Bean
    fun flatFileItemWriter(): FlatFileItemWriter<Customer> {
        return FlatFileItemWriterBuilder<Customer>()
            .name("flatFileItemWriter")
            .resource(FileSystemResource("./output/customer_new.csv"))
            .encoding(OutputKeys.ENCODING)
            .delimited().delimiter("\t")
            .names("Name", "Age", "Gender")
            .append(false)
            .lineAggregator(CustomerLineAggregator())
            .headerCallback(CustomerHeader())
            .footerCallback(CustomerFooter(aggregateInfos))
            .build()
    }


    @Bean
    fun flatFileStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ): Step {
        logger.info { "------------------ Init flatFileStep -----------------" }
        return StepBuilder("flatFileStep", jobRepository)
            .chunk<Customer, Customer>(10, transactionManager)
            .reader(flatFileItemReader())
            .processor(itemProcessor)
            .writer(flatFileItemWriter())
            .build()
    }

    @Bean
    fun flatFileJob(
        flatFileStep: Step,
        jobRepository: JobRepository,
    ): Job {
        logger.info { "------------------ Init flatFileJob -----------------" }
        return JobBuilder(FLAT_FILE_CHUNK_JOB, jobRepository)
            .incrementer(RunIdIncrementer())
            .start(flatFileStep)
            .build()
    }

    companion object {
        const val CHUNK_SIZE = 100
        const val ENCODING = "UTF-8"
        const val FLAT_FILE_CHUNK_JOB = "FLAT_FILE_CHUNK_JOB"
    }
}