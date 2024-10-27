package com.example.domain.entity

import com.example.application.response.PlayerResponse
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Player(
    @BsonId
    val id: ObjectId,
    val pseudo: String,
    val score: Int
) {}