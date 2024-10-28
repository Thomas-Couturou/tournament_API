package com.example

import com.example.domain.entity.Player
import com.example.domain.entity.PlayerWithRank
import com.example.domain.ports.PlayerRepository
import com.google.gson.Gson
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import io.ktor.util.*
import io.mockk.coEvery
import io.mockk.mockk
import org.bson.types.ObjectId
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Test
import org.koin.test.KoinTest
import org.koin.dsl.module
import org.bson.BsonValue
import org.koin.core.context.GlobalContext.loadKoinModules


class ApplicationTest: KoinTest {

    private val repository: PlayerRepository = mockk()

    private val testModule = module {
        single<PlayerRepository> { repository }
    }
    
    @Test
    fun getPlayersSortedByScore_Test() = testApplication {
        environment {
            config = MapApplicationConfig("ktor.environment" to "test")
        }

        application {
            testModule() 
            loadKoinModules(testModule)
        }


        val player1 = Player(ObjectId.get(), "player1", 10)
        val player2 = Player(ObjectId.get(), "player2", 20)
        val playerList = listOf(player1, player2)
        val sortedPlayerList = listOf(PlayerWithRank(player2.pseudo, player2.score, 1), PlayerWithRank(player1.pseudo, player1.score, 2))

        coEvery { repository.getPlayersList() } returns playerList
        coEvery { repository.getPlayersSortedByScore(any<List<Player>>()) } returns sortedPlayerList


        val response = client.get("/player") {
            accept(ContentType.Application.Json)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("[{\"pseudo\":\"player2\",\"score\":20,\"rank\":1},{\"pseudo\":\"player1\",\"score\":10,\"rank\":2}]", bodyAsText())
        }
    }

    @Test
    fun getPlayersSortedByScore_emptyList_returnsEmptyList() = testApplication {
        environment {
            config = MapApplicationConfig("ktor.environment" to "test")
        }

        application {
            testModule()
            loadKoinModules(testModule)
        }

        coEvery { repository.getPlayersList() } returns emptyList()
        coEvery { repository.getPlayersSortedByScore(any<List<Player>>()) } returns emptyList()


        val response = client.get("/player") {
            accept(ContentType.Application.Json)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("[]", bodyAsText())
        }
    }

    @OptIn(InternalAPI::class)
    @Test
    fun postPlayer_Test() = testApplication {
        environment {
            config = MapApplicationConfig("ktor.environment" to "test")
        }

        application {
            testModule()  
            loadKoinModules(testModule)
        }

        val player = Player(ObjectId.get(), "player1", 10)
        val playerList = listOf(player)
        val insertId: BsonValue = mockk()

        coEvery { repository.getPlayersList() } returns playerList
        coEvery { repository.findByPseudo(any(), any()) } returns null
        coEvery { repository.insertOne(any()) } returns insertId

        val response = client.post("/player") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            body = Gson().toJson(player)
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
            assertEquals("Created player with id $insertId", bodyAsText())
        }
    }

    @OptIn(InternalAPI::class)
    @Test
    fun postPlayer_PlayerAlreadyExistst() = testApplication {
        environment {
            config = MapApplicationConfig("ktor.environment" to "test")
        }

        application {
            testModule()  
            loadKoinModules(testModule)
        }

        val player = Player(ObjectId.get(), "player1", 10)
        val playerList = listOf(player)
        val playerWithRank = PlayerWithRank(player.pseudo, player.score, 1)

        coEvery { repository.getPlayersList() } returns playerList
        coEvery { repository.findByPseudo(any(), any()) } returns playerWithRank

        val response = client.post("/player") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            body = Gson().toJson(player)
        }.apply {
            assertEquals(HttpStatusCode.Conflict, status)
            assertEquals("Player already exists", bodyAsText())
        }
    }

    @Test
    fun deletePlayer_Test() = testApplication {
        environment {
            config = MapApplicationConfig("ktor.environment" to "test")
        }

        application {
            testModule()  
            loadKoinModules(testModule)
        }

        val player = Player(ObjectId.get(), "player1", 10)
        val id = player.id.toHexString()

        coEvery { repository.deleteById(any()) } returns 1

        val response = client.delete("/player/byId/$id") {
            accept(ContentType.Application.Json)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Player Deleted successfully", bodyAsText())
        }
    }

    @Test
    fun deletePlayer_NotFound() = testApplication {
        environment {
            config = MapApplicationConfig("ktor.environment" to "test")
        }

        application {
            testModule()  
            loadKoinModules(testModule)
        }

        val player = Player(ObjectId.get(), "player1", 10)
        val playerList = listOf(player)
        val id = player.id.toHexString()

        coEvery { repository.deleteById(any()) } returns 0

        val response = client.delete("/player/byId/$id") {
            accept(ContentType.Application.Json)
        }.apply {
            assertEquals(HttpStatusCode.NotFound, status)
            assertEquals("Player not found", bodyAsText())
        }
    }

    @Test
    fun getPlayerById_Test() = testApplication {
        environment {
            config = MapApplicationConfig("ktor.environment" to "test")
        }

        application {
            testModule()  
            loadKoinModules(testModule)
        }

        val player = Player(ObjectId.get(), "player1", 10)
        val playerList = listOf(player)
        val id = player.id.toHexString()

        coEvery { repository.getPlayersList() } returns playerList
        coEvery { repository.findById(any(), any()) } returns PlayerWithRank(player.pseudo, player.score, 1)

        val response = client.get("/player/byId/$id") {
            accept(ContentType.Application.Json)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("{\"pseudo\":\"player1\",\"score\":10,\"rank\":1}", bodyAsText())
        }
    }

    @Test
    fun getPlayerById_NotFound() = testApplication {
        environment {
            config = MapApplicationConfig("ktor.environment" to "test")
        }

        application {
            testModule()  
            loadKoinModules(testModule)
        }

        val player = Player(ObjectId.get(), "player1", 10)
        val playerList = listOf(player)
        val id = player.id.toHexString()

        coEvery { repository.getPlayersList() } returns playerList
        coEvery { repository.findById(any(), any()) } returns null

        val response = client.get("/player/byId/$id") {
            accept(ContentType.Application.Json)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("No player found for id $id", bodyAsText())
        }
    }

    @OptIn(InternalAPI::class)
    @Test
    fun updatePlayerById_Test() = testApplication {
        environment {
            config = MapApplicationConfig("ktor.environment" to "test")
        }

        application {
            testModule()  
            loadKoinModules(testModule)
        }

        val player = Player(ObjectId.get(), "player1", 10)
        val playerList = listOf(player)
        val id = player.id.toHexString()

        coEvery { repository.updateOne(any(), any()) } returns 1

        val response = client.patch("/player/byId/$id") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            body = Gson().toJson(player)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Player updated successfully", bodyAsText())
        }
    }

    @OptIn(InternalAPI::class)
    @Test
    fun updatePlayerById_NotFound() = testApplication {
        environment {
            config = MapApplicationConfig("ktor.environment" to "test")
        }

        application {
            testModule()  
            loadKoinModules(testModule)
        }

        val player = Player(ObjectId.get(), "player1", 10)
        val id = player.id.toHexString()

        coEvery { repository.updateOne(any(), any()) } returns 0

        val response = client.patch("/player/byId/$id") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            body = Gson().toJson(player)
        }.apply {
            assertEquals(HttpStatusCode.NotFound, status)
            assertEquals("Player not found", bodyAsText())
        }
    }

    @Test
    fun getPlayerByPseudo_Test() = testApplication {
        environment {
            config = MapApplicationConfig("ktor.environment" to "test")
        }

        application {
            testModule()  
            loadKoinModules(testModule)
        }

        val player = Player(ObjectId.get(), "player1", 10)
        val playerList = listOf(player)
        val pseudo = player.pseudo

        coEvery { repository.getPlayersList() } returns playerList
        coEvery { repository.findByPseudo(any(), any()) } returns PlayerWithRank(player.pseudo, player.score, 1)

        val response = client.get("/player/byPseudo/$pseudo") {
            accept(ContentType.Application.Json)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("{\"pseudo\":\"player1\",\"score\":10,\"rank\":1}", bodyAsText())
        }
    }

    @Test
    fun getPlayerByPseudo_NotFound() = testApplication {
        environment {
            config = MapApplicationConfig("ktor.environment" to "test")
        }

        application {
            testModule()  
            loadKoinModules(testModule)
        }

        val player = Player(ObjectId.get(), "player1", 10)
        val playerList = listOf(player)
        val pseudo = player.pseudo

        coEvery { repository.getPlayersList() } returns playerList
        coEvery { repository.findByPseudo(any(), any()) } returns null

        val response = client.get("/player/byPseudo/$pseudo") {
            accept(ContentType.Application.Json)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("No player found for pseudo $pseudo", bodyAsText())
        }
    }

    @OptIn(InternalAPI::class)
    @Test
    fun updatePlayerByPseudo_Test() = testApplication {
        environment {
            config = MapApplicationConfig("ktor.environment" to "test")
        }

        application {
            testModule()  
            loadKoinModules(testModule)
        }

        val player = Player(ObjectId.get(), "player1", 10)
        val pseudo = player.pseudo

        coEvery { repository.updateOneByPseudo(any(), any()) } returns 1

        val response = client.patch("/player/byPseudo/$pseudo") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            body = Gson().toJson(player)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Player updated successfully", bodyAsText())
        }
    }

    @OptIn(InternalAPI::class)
    @Test
    fun updatePlayerByPseudo_NotFound() = testApplication {
        environment {
            config = MapApplicationConfig("ktor.environment" to "test")
        }

        application {
            testModule()  
            loadKoinModules(testModule)
        }

        val player = Player(ObjectId.get(), "player1", 10)
        val pseudo = player.pseudo

        coEvery { repository.updateOneByPseudo(any(), any()) } returns 0

        val response = client.patch("/player/byPseudo/$pseudo") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            body = Gson().toJson(player)
        }.apply {
            assertEquals(HttpStatusCode.NotFound, status)
            assertEquals("Player not found", bodyAsText())
        }
    }

    @Test
    fun deleteAll_test() = testApplication {
        environment {
            config = MapApplicationConfig("ktor.environment" to "test")
        }

        application {
            testModule()  
            loadKoinModules(testModule)
        }

        coEvery { repository.deleteAll() } returns 1

        val response = client.delete("/player") {
            accept(ContentType.Application.Json)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Players Deleted successfully", bodyAsText())
        }
    }

    @Test
    fun deleteAll_NotFound() = testApplication {
        environment {
            config = MapApplicationConfig("ktor.environment" to "test")
        }

        application {
            testModule()  
            loadKoinModules(testModule)
        }

        coEvery { repository.deleteAll() } returns 0

        val response = client.delete("/player") {
            accept(ContentType.Application.Json)
        }.apply {
            assertEquals(HttpStatusCode.NotFound, status)
            assertEquals("No player found", bodyAsText())
        }
    }
}
