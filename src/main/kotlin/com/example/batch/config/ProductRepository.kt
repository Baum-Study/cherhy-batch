package com.example.batch.config

import com.example.batch.model.Product
import org.apache.ibatis.annotations.Mapper

@Mapper
interface ProductRepository {
    fun saveAll(products: List<Product>): Int
}