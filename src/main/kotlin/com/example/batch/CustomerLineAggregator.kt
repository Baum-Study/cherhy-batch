package com.example.batch

import org.springframework.batch.item.file.transform.LineAggregator

class CustomerLineAggregator: LineAggregator<Customer> {
    override fun aggregate(item: Customer) =
        item.name + "," + item.age
}