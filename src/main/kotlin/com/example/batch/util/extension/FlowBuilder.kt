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

open class FlowStatus(
    val name: String,
) {
    companion object {
        val COMPLETED = FlowStatus("COMPLETED")
        val FAILED = FlowStatus("FAILED")
    }
}

class FlowStatusExtension {
    companion object {
        val START = FlowStatus("START")
        val END = FlowStatus("END")
    }
}