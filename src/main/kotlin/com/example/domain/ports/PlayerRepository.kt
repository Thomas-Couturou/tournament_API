package com.example.domain.ports

import com.example.domain.entity.Player
import org.bson.BsonValue
import org.bson.types.ObjectId

interface PlayerRepository {
    suspend fun insertOne(player: Player): BsonValue?
    suspend fun deleteById(objectId: ObjectId): Long
    suspend fun findById(objectId: ObjectId): Player?
    suspend fun updateOne(objectId: ObjectId, player: Player): Long
}