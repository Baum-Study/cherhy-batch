package com.example.batch.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class Mileage(
    val id: Long,
    val customerId: Long,
    val price: BigDecimal,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)