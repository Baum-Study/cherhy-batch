package com.example.batch.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class Product(
    val id: Long,
    val name: String,
    val customerId: Long,
    val price: BigDecimal,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class PurchasedProduct(
    val id: Long,
    val customerId: Long,
    val productId: Long,
    val status: PurchaseStatus,
    val price: BigDecimal,
    val purchasedAt: LocalDateTime,
) {
    constructor(
        id: String,
        customerId: String,
        productId: String,
        status: String,
        price: String,
        purchasedAt: String,
    ) : this(
        id.toLong(),
        customerId.toLong(),
        productId.toLong(),
        PurchaseStatus.valueOf(status),
        price.toBigDecimal(),
        LocalDateTime.parse(purchasedAt),
    )
}

enum class PurchaseStatus {
    PURCHASED,
    CANCELED,
    DELIVERED,
    ;
}