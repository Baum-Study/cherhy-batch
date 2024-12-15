package com.example.batch.model

import com.example.batch.config.BatchMapper
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
): BatchMapper {
    val id: Long = 0

    override fun toMap(): Map<String, Any?> =
        mapOf(
            "id" to id,
            "sellerId" to sellerId,
            "amount" to amount,
            "settlementDate" to settlementDate,
        )
}