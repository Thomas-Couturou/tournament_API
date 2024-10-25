package com.example.infrastructure.repository

import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import com.example.domain.entity.Player
import com.example.domain.ports.PlayerRepository
import com.mongodb.client.model.Sorts
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.BsonValue
import org.bson.types.ObjectId

class PlayerRepositoryImpl (
    private val mongoDatabase: MongoDatabase
): PlayerRepository{
    companion object {
        const val PLAYER_COLLECTION = "player"
    }

    override suspend fun insertOne(player: Player): BsonValue? {
        try {
            val result = mongoDatabase.getCollection<Player>(PLAYER_COLLECTION).insertOne(player)
            return result.insertedId
        } catch (e: MongoException) {
            System.err.println("Unable to insert due to an error: $e")
        }
        return null
    }

    override suspend fun deleteById(objectId: ObjectId): Long {
        try {
            val result = mongoDatabase.getCollection<Player>(PLAYER_COLLECTION).deleteOne(Filters.eq("_id", objectId))
            return result.deletedCount
        } catch (e: MongoException) {
            System.err.println("Unable to delete due to an error: $e")
        }
        return 0
    }

    override suspend fun findById(objectId: ObjectId): Player? =
        mongoDatabase.getCollection<Player>(PLAYER_COLLECTION).withDocumentClass<Player>()
        .find(Filters.eq("_id", objectId))
        .firstOrNull()

    override suspend fun updateOne(objectId: ObjectId, player: Player): Long {
        try {
            val query = Filters.eq("_id", objectId)
            val updates = Updates.combine(
                Updates.set(Player::pseudo.name, player.pseudo),
                Updates.set(Player::score.name, player.score)
            )
            val options = UpdateOptions().upsert(true)
            val result = mongoDatabase.getCollection<Player>(PLAYER_COLLECTION).updateOne(query, updates, options)
            return result.modifiedCount
        } catch (e: MongoException) {
            System.err.println("Unable to update due to an error: $e")
        }
        return 0
    }

    override suspend fun getPlayersSortedByScore(): List<Player> =
        mongoDatabase.getCollection<Player>(PLAYER_COLLECTION).withDocumentClass<Player>()
            .find().sort(Sorts.descending("score")).toList()

    override suspend fun findByPseudo(pseudo: String): Player? =
        mongoDatabase.getCollection<Player>(PLAYER_COLLECTION).withDocumentClass<Player>()
            .find(Filters.eq("pseudo", pseudo))
            .firstOrNull()

    override suspend fun updateOneByPseudo(pseudo: String, player: Player): Long {
        try {
            val query = Filters.eq("pseudo", pseudo)
            val updates = Updates.combine(
                Updates.set(Player::score.name, player.score)
            )
            val options = UpdateOptions().upsert(true)
            val result = mongoDatabase.getCollection<Player>(PLAYER_COLLECTION).updateOne(query, updates, options)
            return result.modifiedCount
        } catch (e: MongoException) {
            System.err.println("Unable to update due to an error: $e")
        }
        return 0
    }

    override suspend fun deleteAll(): Long {
        try {
            val result = mongoDatabase.getCollection<Player>(PLAYER_COLLECTION).deleteMany(Filters.empty())
            return result.deletedCount
        } catch (e: MongoException) {
            System.err.println("Unable to delete due to an error: $e")
        }
        return 0
    }
}