package com.example.batch

import java.math.BigDecimal

fun <T, R> Map<T, Iterable<R>>.sumOf(
    bigDecimalSelector: (R) -> BigDecimal,
) =
    this.map { (key, value) ->
        key to value.sumOf(bigDecimalSelector)
    }.toMap()