package com.example.application.request

import com.example.domain.entity.Player
import org.bson.types.ObjectId

data class PlayerRequestUpdate (
    val score: Int
    ){
        fun toDomain(): Player {
            return Player(
                id = ObjectId(),
                pseudo = "",
                score = score
            )
        }
}