package com.example.batch.lib

import org.jeasy.random.EasyRandomParameters
import org.jeasy.random.api.Randomizer
import java.util.stream.LongStream
import kotlin.reflect.KProperty

internal fun <T, R> EasyRandomParameters.randomize(
    property: KProperty<T>,
    randomizer: Randomizer<R>,
) =
    randomize(
        { it.name == property.name },
        randomizer,
    )

internal fun <T> Number.mapParallel(
    block: () -> T,
) =
    LongStream.range(0, this.toLong())
        .parallel()
        .mapToObj {
            block()
        }
        .toList()