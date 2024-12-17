package com.example.batch.model

import jakarta.persistence.*

@Entity
@Table(name = "image")
class Image(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    val url: String,

    @ManyToOne
    @JoinColumn(name = "customer_id")
    val customer: Customer,
)