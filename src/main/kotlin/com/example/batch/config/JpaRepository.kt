package com.example.batch.config

import com.example.batch.model.Customer
import com.example.batch.model.Image
import com.example.batch.model.Status
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerRepository : JpaRepository<Customer, Long> {
    fun findByStatus(status: Status): List<Customer>
    fun countByStatus(status: Status): Long
}

interface ImageRepository : JpaRepository<Image, Long> {
    fun countByCustomerIn(customers: List<Customer>): Long
}