package com.example.batch.lib

import com.example.batch.model.*
import com.example.batch.model.PurchaseStatus.PURCHASED
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.arbitrary.next
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

internal object ProductFactory {
    fun generateRandomV2(
        customerId: Long,
    ) =
        Product(
            id = 0,
            name = Arb.string(1..10).next(),
            customerId = customerId,
            price = Arb.long(1000L..10000).next().toBigDecimal(),
            createdAt = Arb.localDateTime(2024, 2024).next(),
            updatedAt = Arb.localDateTime(2024, 2024).next(),
        )
}

internal object PurchaseFactory {
    fun generateRandomV2(
        customerId: Long,
        product: Product,
    ) =
        PurchasedProduct(
            id = 0,
            customerId = customerId,
            productId = product.id,
            status = PURCHASED,
            price = product.price,
            purchasedAt = Arb.localDateTime(2024, 2024).next(),
        )
}

internal object MileageFactory {
    fun generateRandomV2(
        customerId: Long,
    ) =
        Mileage(
            id = 0,
            customerId = customerId,
            price = Arb.long(1000L..10000).next().toBigDecimal(),
            createdAt = Arb.localDateTime(2024, 2024).next(),
            updatedAt = Arb.localDateTime(2024, 2024).next(),
        )
}