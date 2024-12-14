package com.example.batch.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.support.JdbcTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
class DataSourceConfig(
    private val dataSource: DataSource,
) {
    @Bean
    fun transactionManager(): PlatformTransactionManager {
        return JdbcTransactionManager(dataSource)
    }
}