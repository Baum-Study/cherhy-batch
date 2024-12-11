package com.example.batch.lib

import org.springframework.batch.core.JobParametersBuilder

object JobParameterFactory {
    fun create(
        jobName: String,
    ) =
        JobParametersBuilder()
            .addString(JOB_NAME, jobName)
            .addLong(RUN_ID, System.currentTimeMillis())
            .toJobParameters()

    private const val JOB_NAME = "job.name"
    private const val RUN_ID = "run.id"
}