package com.example.batch.extension

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.math.BigDecimal
import java.sql.ResultSet

fun <T, R> Map<T, Iterable<R>>.sumOf(
    bigDecimalSelector: (R) -> BigDecimal,
) =
    this.map { (key, value) ->
        key to value.sumOf(bigDecimalSelector)
    }.toMap()

@Suppress("SqlSourceToSinkFlow")
fun <T> JdbcTemplate.sql(
    query: String,
    params: Map<String, Any?> = emptyMap(),
    mapper: ResultSet.() -> T
): List<T> =
    NamedParameterJdbcTemplate(this)
        .query(query, params) { rs, _ ->
            mapper(rs)
        }

@Suppress("SqlSourceToSinkFlow")
fun JdbcTemplate.batchUpdate(
    query: String,
    paramsList: List<Map<String, Any?>>
): IntArray {
    val namedTemplate = NamedParameterJdbcTemplate(this)
    return namedTemplate.batchUpdate(query, paramsList.toTypedArray())
}
