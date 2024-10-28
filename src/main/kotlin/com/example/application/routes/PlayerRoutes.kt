package com.example.application.routes

import com.example.application.request.PlayerRequestCreate

import com.example.domain.ports.PlayerRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.route
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import org.bson.types.ObjectId
import org.koin.ktor.ext.inject


fun Route.playerRoutes() {
    val repository by inject<PlayerRepository>()

    route("/player") {

        get {
            val players = repository.getPlayersList()
            val sortedPlayers = repository.getPlayersSortedByScore(players)
            call.respond(sortedPlayers)
        }

        post {
            val player = call.receive<PlayerRequestCreate>()
            val players = repository.getPlayersList()
            if (repository.findByPseudo(player.pseudo, players) == null){
                val insertedId = repository.insertOne(player.toDomain())
                call.respond(HttpStatusCode.Created, "Created player with id $insertedId")
            }
            else {
                call.respondText("Player already exists", status = HttpStatusCode.Conflict)
            }
        }

        delete("byId/{id?}") {
            val id = call.parameters["id"] ?: return@delete call.respondText(
                text = "Missing player id",
                status = HttpStatusCode.BadRequest
            )
            val delete: Long = repository.deleteById(ObjectId(id))
            if (delete == 1L) {
                return@delete call.respondText("Player Deleted successfully", status = HttpStatusCode.OK)
            }
            return@delete call.respondText("Player not found", status = HttpStatusCode.NotFound)
        }

        get("byId/{id?}") {
            val id = call.parameters["id"]
            if (id.isNullOrEmpty()) {
                return@get call.respondText(
                    text = "Missing id",
                    status = HttpStatusCode.BadRequest
                )
            }
            val players = repository.getPlayersList()
            repository.findById(ObjectId(id), players)?.let {
                call.respond(it.toResponse())
            } ?: call.respondText("No player found for id $id")
        }

        patch("byId/{id?}") {
            val id = call.parameters["id"] ?: return@patch call.respondText(
                text = "Missing player id",
                status = HttpStatusCode.BadRequest
            )
            val updated = repository.updateOne(ObjectId(id), call.receive())
            call.respondText(
                text = if (updated == 1L) "Player updated successfully" else "Player not found",
                status = if (updated == 1L) HttpStatusCode.OK else HttpStatusCode.NotFound
            )
        }

        get("byPseudo/{pseudo?}") {
            val pseudo = call.parameters["pseudo"]
            if (pseudo.isNullOrEmpty()) {
                return@get call.respondText(
                    text = "Missing pseudo",
                    status = HttpStatusCode.BadRequest
                )
            }
            val players = repository.getPlayersList()
            repository.findByPseudo(pseudo, players)?.let {
                call.respond(it.toResponse())
            } ?: call.respondText("No player found for pseudo $pseudo")
        }

        patch("byPseudo/{pseudo?}") {
            val pseudo = call.parameters["pseudo"] ?: return@patch call.respondText(
                text = "Missing player pseudo",
                status = HttpStatusCode.BadRequest
            )
            val updated = repository.updateOneByPseudo(pseudo, call.receive())
            call.respondText(
                text = if (updated == 1L) "Player updated successfully" else "Player not found",
                status = if (updated == 1L) HttpStatusCode.OK else HttpStatusCode.NotFound
            )
        }

        delete {
            val delete: Long = repository.deleteAll()
            if (delete >= 1L) {
                return@delete call.respondText("Players Deleted successfully", status = HttpStatusCode.OK)
            }
            return@delete call.respondText("No player found", status = HttpStatusCode.NotFound)
        }
    }
}