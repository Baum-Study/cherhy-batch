package com.example.batch

import io.kotest.core.spec.style.StringSpec
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jeasy.random.api.Randomizer
import org.jeasy.random.randomizers.text.StringRandomizer
import java.util.stream.LongStream
import kotlin.random.Random
import kotlin.reflect.KProperty

class CreateDummyDataTests : StringSpec({
    "Create dummy data" {
        100_000.mapParallel {
            val parameter = createInputModelParameter()
            EasyRandom(parameter).nextObject(InputModel::class.java)
        }
    }
})

internal fun createInputModelParameter() =
    EasyRandomParameters()
        .randomize(InputModel::name) {
            StringRandomizer(1, 10, Random.nextLong(1, 100)).randomValue
        }
        .randomize(InputModel::id) {
            (100..1_000_000).random()
        }


internal fun <T> Number.mapParallel(
    block: () -> T,
) =
    LongStream.range(0, this.toLong())
        .parallel()
        .mapToObj {
            block()
        }
        .toList()

internal fun <T, R> EasyRandomParameters.randomize(
    property: KProperty<T>,
    randomizer: Randomizer<R>,
) =
    randomize(
        { it.name == property.name },
        randomizer,
    )