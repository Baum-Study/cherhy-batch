package com.example.batch.util.extension

import org.springframework.batch.core.job.builder.FlowBuilder
import org.springframework.batch.core.job.builder.SimpleJobBuilder

fun SimpleJobBuilder.on(
    status: FlowStatus,
) =
    this.on(status.name)

fun <T> FlowBuilder<T>.on(
    status: FlowStatus,
) =
    this.on(status.name)

enum class FlowStatus {
    COMPLETED,
    FAILED,
}