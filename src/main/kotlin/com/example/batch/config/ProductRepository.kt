package com.example.batch.config

import com.example.batch.model.PurchasedProduct
import org.apache.ibatis.annotations.Mapper

@Mapper
interface ProductRepository {
    fun selectProducts(): List<PurchasedProduct>
    fun updateProduct(product: PurchasedProduct)
}