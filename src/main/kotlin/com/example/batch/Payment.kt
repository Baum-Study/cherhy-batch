package com.example.batch

import java.math.BigDecimal
import java.time.LocalDateTime

data class Payment(
    val id: Long,
    val sellerId: Long,
    val productId: Long,
    val productName: String,
    val price: BigDecimal,
    val paymentDate: LocalDateTime,
)
