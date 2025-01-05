package com.example.batch.config

import com.example.batch.model.Mileage
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import java.math.BigDecimal

@Mapper
interface MileageRepository {
    @Update(
        """
            UPDATE mileage SET price = #{price}
            WHERE customer_id = #{customerId}
        """
    )
    fun update(
        customerId: Long,
        price: BigDecimal,
    )

    @Insert(
        """
        <script>
        INSERT INTO mileage (customer_id, price, created_at, updated_at)
        VALUES
        <foreach collection="mileages" item="mileage" separator=",">
            (#{mileage.customerId}, #{mileage.price}, #{mileage.createdAt}, #{mileage.updatedAt})
        </foreach>
        </script>
    """
    )
    fun saveAll(mileages: List<Mileage>): Int

    @Select("SELECT * FROM mileage")
    fun findAll(): List<Mileage>
}