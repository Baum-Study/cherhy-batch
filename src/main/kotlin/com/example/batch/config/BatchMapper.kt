package com.example.batch.config

interface BatchMapper {
    fun toMap(): Map<String, Any?>
}