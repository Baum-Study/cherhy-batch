package com.example.batch.processer

import com.example.batch.model.Customer
import org.springframework.batch.item.file.transform.LineAggregator

class CustomerLineAggregator: LineAggregator<Customer> {
    override fun aggregate(item: Customer) =
        item.name + "," + item.age
}