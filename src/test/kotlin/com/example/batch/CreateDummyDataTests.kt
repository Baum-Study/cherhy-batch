package com.example.batch

import com.example.batch.lib.PaymentFactory
import com.example.batch.lib.mapParallel
import io.kotest.core.spec.style.StringSpec

class CreateDummyDataTests : StringSpec({
    "Create dummy data" {
        val data = 100.mapParallel(PaymentFactory::generateRandom)
        println("data: $data")
    }
})