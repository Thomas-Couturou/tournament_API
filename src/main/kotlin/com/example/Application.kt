package com.example

import com.example.application.routes.playerRoutes
import com.example.domain.ports.PlayerRepository
import com.example.infrastructure.repository.PlayerRepositoryImpl
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.ktor.http.*
import io.ktor.serialization.gson.gson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.routing
import io.ktor.server.tomcat.EngineMain
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    install(CORS) {
        allowHost("localhost:4200", schemes = listOf("http"))
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
    }
    install(ContentNegotiation) {
        gson {  }
    }
    install(Koin) {
        slf4jLogger()
        modules(module {
            single {MongoClient.create(
                environment.config.propertyOrNull("ktor.mongo.uri")?.getString() ?: throw RuntimeException("Failed to access MongoDB URI.")
            )}
            single {get<MongoClient>().getDatabase(environment.config.property("ktor.mongo.database").getString())}
        }, module {
            single<PlayerRepository> {PlayerRepositoryImpl(get())}
        })
    }
    routing {
        swaggerUI(path = "swagger-ui", swaggerFile = "openapi/documentation.yaml") {
            version = "4.15.5"
        }
        playerRoutes()
    }
}
