package com.example.batch

import com.example.batch.lib.PaymentFactory
import com.example.batch.lib.mapParallel
import io.kotest.core.spec.style.StringSpec
import java.io.File
import java.io.PrintWriter

// 참고 했던 블로그 : https://jojoldu.tistory.com/525
class CreateFileTests : StringSpec({
    "랜덤 데이터를 생성해서 csv 파일을 만든다." {
        val payments = 10_000.mapParallel(PaymentFactory::generateRandom)
        val reader = LinkedListItemReader(payments)

        val file = File("output/test-payments.csv")
        file.parentFile.mkdirs()

        PrintWriter(file).use { writer ->
            writer.println("id,sellerId,productId,productName,price,paymentDate")

            var payment = reader.read()

            while (payment != null) {
                writer.println("${payment!!.id},${payment!!.sellerId},${payment!!.productId},${payment!!.productName},${payment!!.price},${payment!!.paymentDate}")
                payment = reader.read()
            }
        }
    }
})