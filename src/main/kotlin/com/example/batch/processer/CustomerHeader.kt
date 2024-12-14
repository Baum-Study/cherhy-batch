package com.example.batch.processer

import org.springframework.batch.item.file.FlatFileHeaderCallback
import java.io.Writer

class CustomerHeader: FlatFileHeaderCallback {
    override fun writeHeader(writer: Writer) {
        writer.write("ID,AGE")
    }
}