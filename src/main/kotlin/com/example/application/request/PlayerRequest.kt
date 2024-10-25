package com.example.application.request

import com.example.domain.entity.Player
import org.bson.types.ObjectId

data class PlayerRequest (
    val pseudo: String
){
    fun toDomain(): Player {
        return Player(
            id = ObjectId(),
            pseudo = pseudo,
            score = 0
        )
    }
}