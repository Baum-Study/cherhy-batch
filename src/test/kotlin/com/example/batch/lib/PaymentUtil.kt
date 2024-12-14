package com.example.batch.lib

import com.example.batch.model.Payment
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jeasy.random.randomizers.text.StringRandomizer
import java.time.LocalDateTime
import kotlin.random.Random

internal object PaymentFactory {
    fun generateRandom(): Payment {
        val parameter = EasyRandomParameters()
            .excludeField {
                it.name == Payment::id.name
            }
            .randomize(Payment::sellerId) {
                (1..100).random()
            }
            .randomize(Payment::productId) {
                (1..100).random()
            }
            .randomize(Payment::productName) {
                StringRandomizer(1, 10, Random.nextLong(1, 100)).randomValue
            }
            .randomize(Payment::price) {
                (100..1_000_000).random().toBigDecimal()
            }
            .randomize(Payment::paymentDate) {
                LocalDateTime.of(
                    Random.nextInt(2020, 2022),
                    Random.nextInt(1, 13),
                    Random.nextInt(1, 29),
                    Random.nextInt(0, 24),
                    Random.nextInt(0, 60),
                    Random.nextInt(0, 60),
                )
            }
        return EasyRandom(parameter).nextObject(Payment::class.java)
    }
}