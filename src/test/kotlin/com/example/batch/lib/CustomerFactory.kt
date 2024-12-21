package com.example.batch.lib

import com.example.batch.model.Customer
import com.example.batch.model.Image
import com.example.batch.model.Status
import io.kotest.property.Arb
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jeasy.random.randomizers.text.StringRandomizer
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random

internal object CustomerFactory {
    fun generateRandom(): Customer {
        val parameters = EasyRandomParameters()
            .randomize(Int::class.java) {
                ThreadLocalRandom.current().nextInt(0, 100)
            }
            .excludeField {
                it.name == Customer::id.name
            }
            .randomize(String::class.java) {
                StringRandomizer(1, 10, Random.nextLong(1, 100)).randomValue
            }
            .randomize(Status::class.java) {
                if (Random.nextBoolean()) Status.ACTIVE
                else Status.INACTIVE
            }

        val easyRandom = EasyRandom(parameters)
        return easyRandom.nextObject(Customer::class.java)
    }

    fun generateRandomV2() =
        Customer(
            id = 0,
            name = Arb.string(1..10).next(),
            age = Arb.int(10..90).next(),
            status = Arb.enum<Status>().next(),
        )
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

    fun generateRandomV2(
        customer: Customer,
    ) = Image(
        id = 0,
        url = Arb.string(1..10).next(),
        customer = customer,
    )
}