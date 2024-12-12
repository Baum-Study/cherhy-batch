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

data class Settlement(
    val sellerId: Long,
    val amount: BigDecimal,
    val settlementDate: LocalDateTime,
) {
    val id: Long = 0

    val toMap: Map<String, Any?> = mapOf(
        "id" to id,
        "sellerId" to sellerId,
        "amount" to amount,
        "settlementDate" to settlementDate,
    )
}