package com.example.batch.config

import com.example.batch.model.PurchaseStatus
import com.example.batch.model.PurchasedProduct
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import java.time.LocalDateTime

@Mapper
interface PurchaseRepository {
    fun saveAll(purchasedProducts: List<PurchasedProduct>): Int

    @Select(
        """
            SELECT * FROM purchased_product
            WHERE status = #{status}
            AND purchased_at < #{date}
        """
    )
    fun findAll(
        @Param("status") status: PurchaseStatus,
        @Param("date") date: LocalDateTime = LocalDateTime.now(),
    ): List<PurchasedProduct>

    @Update(
        """
            UPDATE purchased_product
            SET status = #{status}
            WHERE id = #{id}
        """
    )
    fun update(purchasedProduct: PurchasedProduct)

    @Update(
        """
            UPDATE purchased_product
            SET status = 'DELIVERED'
            WHERE id = #{id}
        """
    )
    fun updateDeliveryComplete(purchasedProduct: PurchasedProduct)
}