package com.example

import com.example.application.routes.playerRoutes
import com.example.domain.ports.PlayerRepository
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import io.mockk.mockk

fun Application.testModule() {
    // Création d'un mock pour le PlayerRepository
    val playerRepository: PlayerRepository = mockk(relaxed = true)

    install(ContentNegotiation) {
        gson {  }
    }

    install(Koin) {
        slf4jLogger()
        modules(
            module {
                single<PlayerRepository> { playerRepository }
            }
        )
    }
    routing {
        playerRoutes()
    }
}