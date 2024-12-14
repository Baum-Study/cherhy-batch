package com.example.batch.util

import org.springframework.aop.support.AopUtils
import org.springframework.batch.item.ItemReader
import java.util.*

class LinkedListItemReader<T>(
    list: MutableList<T>,
) : ItemReader<T> {
    private var list: MutableList<T> = mutableListOf()

    init {
        if (AopUtils.isAopProxy(list)) this.list = list
        else this.list = LinkedList(list)
    }

    override fun read() =
        if (list.isNotEmpty()) list.removeAt(0)
        else null
}