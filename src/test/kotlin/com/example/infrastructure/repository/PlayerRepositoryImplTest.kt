package com.example.infrastructure.repository

import com.example.domain.entity.Player
import com.mongodb.MongoException
import com.mongodb.client.model.InsertOneOptions
import com.mongodb.client.result.InsertOneResult
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.bson.BsonObjectId
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PlayerRepositoryImplTest {
    private val mockDatabase: MongoDatabase = mockk()
    private val repository = PlayerRepositoryImpl(mockDatabase)

    @Test
    fun insertOneSuccess() = runBlocking {
        val player = Player(ObjectId(), "Player1", 100)
        val insertedId = BsonObjectId(ObjectId())
        val insertOneResult = mockk<InsertOneResult>()
        every { insertOneResult.insertedId } returns insertedId
        coEvery { mockDatabase.getCollection<Player>("player").insertOne(player, any<InsertOneOptions>()) } returns insertOneResult
        val result = repository.insertOne(player)

        assertEquals(insertedId, result)
    }

    @Test
    fun insertOneFailure() = runBlocking {
        val player = Player(ObjectId(), "Player1", 100)
        coEvery { mockDatabase.getCollection<Player>("player").insertOne(player, any<InsertOneOptions>()) } throws MongoException("Failed to insert player")
        val result = repository.insertOne(player)

        assertNull(result)
    }

    @Test
    fun deleteByIdSuccess() = runBlocking {
        val playerId = ObjectId()
        val deleteResult = mockk<com.mongodb.client.result.DeleteResult>()
        every { deleteResult.deletedCount } returns 1L
        coEvery { mockDatabase.getCollection<Player>("player").deleteOne(any<Bson>(), any()) } returns deleteResult
        val result = repository.deleteById(playerId)

        assertEquals(1L, result)
    }

    @Test
    fun deleteByIdFailure() = runBlocking {
        val playerId = ObjectId()
        coEvery { mockDatabase.getCollection<Player>("player").deleteOne(any<Bson>(), any()) } throws MongoException("Failed to delete player")
        val result = repository.deleteById(playerId)

        assertEquals(0, result)
    }

    @Test
    fun findByIdFoundPlayer() = runBlocking {
        val playerId = ObjectId()
        val players = listOf(
            Player(ObjectId(), "Player1", 300),
            Player(playerId, "Player2", 200),
            Player(ObjectId(), "Player3", 100)
        )

        val result = repository.findById(playerId, players)

        assertNotNull(result)
        assertEquals("Player2", result?.pseudo)
        assertEquals(200, result?.score)
        assertEquals(2, result?.rank)
    }

    @Test
    fun findByIdPlayerNotFound() = runBlocking {
        val playerId = ObjectId()
        val players = listOf(
            Player(ObjectId(), "Player1", 300),
            Player(ObjectId(), "Player2", 200),
            Player(ObjectId(), "Player3", 100)
        )

        val result = repository.findById(playerId, players)

        assertNull(result)
    }

    @Test
    fun updateOneSuccess() = runBlocking {
        val playerId = ObjectId()
        val player = Player(playerId, "Player1", 100)
        val updateResult = mockk<com.mongodb.client.result.UpdateResult>()
        every { updateResult.modifiedCount } returns 1L
        coEvery { mockDatabase.getCollection<Player>("player").updateOne(any<Bson>(), any<Bson>(), any()) } returns updateResult
        val result = repository.updateOne(playerId, player)

        assertEquals(1L, result)
    }

    @Test
    fun updateOneFailure() = runBlocking {
        val playerId = ObjectId()
        val player = Player(playerId, "Player1", 100)
        coEvery { mockDatabase.getCollection<Player>("player").updateOne(any<Bson>(), any<Bson>(), any()) } throws MongoException("Failed to update player")
        val result = repository.updateOne(playerId, player)

        assertEquals(0, result)
    }

    @Test
    fun getPlayersSortedByScoreTest() = runBlocking {
        val players = listOf(
            Player(ObjectId(), "Player1", 300),
            Player(ObjectId(), "Player2", 200),
            Player(ObjectId(), "Player3", 100)
        )
        val result = repository.getPlayersSortedByScore(players)

        assertEquals(players.size, result.size)
        assertEquals("Player1", result[0].pseudo)
        assertEquals(300, result[0].score)
        assertEquals(1, result[0].rank)
        assertEquals("Player2", result[1].pseudo)
        assertEquals(200, result[1].score)
        assertEquals(2, result[1].rank)
        assertEquals("Player3", result[2].pseudo)
        assertEquals(100, result[2].score)
        assertEquals(3, result[2].rank)
    }

    @Test
    fun findByPseudoFoundPlayer() = runBlocking {
        val players = listOf(
            Player(ObjectId(), "Player1", 300),
            Player(ObjectId(), "Player2", 200),
            Player(ObjectId(), "Player3", 100)
        )

        val result = repository.findByPseudo("Player2", players)

        assertNotNull(result)
        assertEquals("Player2", result?.pseudo)
        assertEquals(200, result?.score)
        assertEquals(2, result?.rank)
    }

    @Test
    fun findByPseudoPlayerNotFound() = runBlocking {
        val players = listOf(
            Player(ObjectId(), "Player1", 300),
            Player(ObjectId(), "Player2", 200),
            Player(ObjectId(), "Player3", 100)
        )

        val result = repository.findByPseudo("Player4", players)

        assertNull(result)
    }

    @Test
    fun updateOneByPseudoSuccess() = runBlocking {
        val player = Player(ObjectId(), "Player1", 100)
        val updateResult = mockk<com.mongodb.client.result.UpdateResult>()
        every { updateResult.modifiedCount } returns 1L
        coEvery { mockDatabase.getCollection<Player>("player").updateOne(any<Bson>(), any<Bson>(), any()) } returns updateResult
        val result = repository.updateOneByPseudo("Player1", player)

        assertEquals(1L, result)
    }

    @Test
    fun updateOneByPseudoFailure() = runBlocking {
        val player = Player(ObjectId(), "Player1", 100)
        coEvery { mockDatabase.getCollection<Player>("player").updateOne(any<Bson>(), any<Bson>(), any()) } throws MongoException("Failed to update player")
        val result = repository.updateOneByPseudo("Player1", player)

        assertEquals(0, result)
    }

    @Test
    fun deleteAllSuccess() = runBlocking {
        val deleteResult = mockk<com.mongodb.client.result.DeleteResult>()
        every { deleteResult.deletedCount } returns 4L
        coEvery { mockDatabase.getCollection<Player>("player").deleteMany(any<Bson>(), any()) } returns deleteResult
        val result = repository.deleteAll()

        assertEquals(4L, result)
    }

    @Test
    fun deleteAllFailure() = runBlocking {
        coEvery { mockDatabase.getCollection<Player>("player").deleteMany(any<Bson>(), any()) } throws MongoException("Failed to delete players")
        val result = repository.deleteAll()

        assertEquals(0, result)
    }

}