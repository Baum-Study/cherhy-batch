package com.example.batch

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
        println(dataSource.connection.metaData.url)
        println(dataSource.connection.metaData.driverName)
        println(dataSource.connection.metaData.userName)
        return JdbcTransactionManager(dataSource)
    }
}