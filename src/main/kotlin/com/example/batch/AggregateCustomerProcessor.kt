package com.example.batch

import org.springframework.batch.item.ItemProcessor
import java.util.concurrent.ConcurrentHashMap

class AggregateCustomerProcessor(
    private val aggregateCustomers: ConcurrentHashMap<String, Int>,
): ItemProcessor<Customer, Customer> {
    override fun process(item: Customer): Customer {
        aggregateCustomers.putIfAbsent("TOTAL_CUSTOMERS", 0)
        aggregateCustomers.putIfAbsent("TOTAL_AGES", 0)
        aggregateCustomers["TOTAL_CUSTOMERS"] = aggregateCustomers["TOTAL_CUSTOMERS"]!! + 1
        aggregateCustomers["TOTAL_AGES"] = aggregateCustomers["TOTAL_AGES"]!! + item.age
        return item
    }
}