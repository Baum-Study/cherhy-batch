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

    @Enumerated(EnumType.STRING)
    val status: Status,
) {
    override fun toString(): String {
        return "Customer(id=$id, name='$name', age=$age, status=$status)"
    }
}

enum class Status {
    ACTIVE,
    INACTIVE,
    ;
}