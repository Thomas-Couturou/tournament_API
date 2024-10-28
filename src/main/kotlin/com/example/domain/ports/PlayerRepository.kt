package com.example.domain.ports

import com.example.domain.entity.Player
import com.example.domain.entity.PlayerWithRank
import org.bson.BsonValue
import org.bson.types.ObjectId

interface PlayerRepository {
    suspend fun insertOne(player: Player): BsonValue?
    suspend fun deleteById(objectId: ObjectId): Long
    suspend fun findById(objectId: ObjectId, players: List<Player>): PlayerWithRank?
    suspend fun updateOne(objectId: ObjectId, player: Player): Long
    suspend fun getPlayersSortedByScore(players: List<Player>): List<PlayerWithRank>
    suspend fun findByPseudo(pseudo: String, players: List<Player>): PlayerWithRank?
    suspend fun updateOneByPseudo(pseudo: String, player: Player): Long
    suspend fun deleteAll(): Long
    suspend fun getPlayersList(): List<Player>
}