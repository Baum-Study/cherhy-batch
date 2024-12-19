package com.example.batch.model

import jakarta.persistence.*

@Entity
@Table(name = "image")
class Image(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    val url: String,

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    val customer: Customer,
) {
    override fun toString(): String {
        return "Image(id=$id, url='$url', customer=$customer)"
    }
}