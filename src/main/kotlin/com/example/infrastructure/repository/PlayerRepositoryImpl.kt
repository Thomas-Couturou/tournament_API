package com.example.infrastructure.repository

import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import com.example.domain.entity.Player
import com.example.domain.entity.PlayerWithRank
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

    override suspend fun findById(objectId: ObjectId, players: List<Player>): PlayerWithRank? {

        var index = 1;
        var result: PlayerWithRank? = null;
        for (player in players){
            if (player.id == objectId){
                result = PlayerWithRank(player.pseudo, player.score, index)
            }
            index += 1;
        }
        return result
    }

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

    override suspend fun getPlayersSortedByScore(players: List<Player>): List<PlayerWithRank> {

        val playersWithRank = mutableListOf<PlayerWithRank>()
        var index = 1
        for (player in players){
            playersWithRank.add(PlayerWithRank(player.pseudo, player.score, index))
            index += 1
        }
        return playersWithRank
    }

    override suspend fun findByPseudo(pseudo: String, players: List<Player>): PlayerWithRank? {

        var index = 1;
        var result: PlayerWithRank? = null;
        for (player in players){
            if (player.pseudo == pseudo){
                result = PlayerWithRank(player.pseudo, player.score, index)
            }
            index += 1;
        }
        return result

    }

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

    override suspend fun getPlayersList(): List<Player> {
        return mongoDatabase.getCollection<Player>(PLAYER_COLLECTION)
            .find().sort(Sorts.descending("score")).toList()
    }
}