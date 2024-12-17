package com.example.batch.lib

import com.example.batch.model.Customer
import com.example.batch.model.Image
import com.example.batch.model.Status
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jeasy.random.randomizers.text.StringRandomizer
import kotlin.random.Random

internal object CustomerFactory {
    fun generateRandom(): Customer {
        val parameter = EasyRandomParameters()
            .excludeField {
                it.name == Customer::id.name
            }
            .randomize(Customer::name) {
                StringRandomizer(1, 10, Random.nextLong(1, 100)).randomValue
            }
            .randomize(Customer::age) {
                (1..100).random()
            }
            .randomize(Customer::status) {
                if (Random.nextBoolean()) {
                    Status.ACTIVE
                } else {
                    Status.INACTIVE
                }
            }
        return EasyRandom(parameter).nextObject(Customer::class.java)
    }
}

internal object ImageFactory {
    fun generateRandom(
        customer: Customer,
    ): Image {
        val parameter = EasyRandomParameters()
            .excludeField {
                it.name == Image::id.name
            }
            .randomize(Image::url) {
                StringRandomizer(1, 10, Random.nextLong(1, 100)).randomValue
            }
            .randomize(Image::customer) {
                customer
            }
        return EasyRandom(parameter).nextObject(Image::class.java)
    }
}