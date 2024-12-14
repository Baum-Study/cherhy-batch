package com.example.batch.model

import jakarta.persistence.*

@Entity
@Table(name = "customer")
class Customer(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    val name: String,

    val age: Int,

    val gender: String,
)