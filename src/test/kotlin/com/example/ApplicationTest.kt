package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.*

class ApplicationTest {
    @Test
    fun test(){

        println("System properties:")
        System.getProperties().forEach { key, value ->
            println("$key = $value")
        }

        val mongoUri = System.getProperty("MONGO_URI") ?: "mongodb://localhost:27017/defaultdb"
        println("Mongo URI: $mongoUri")
    }
}
