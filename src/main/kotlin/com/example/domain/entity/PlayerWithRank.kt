package com.example.domain.entity

import com.example.application.response.PlayerResponse

class PlayerWithRank (
    val pseudo: String,
    val score: Int,
    val rank: Int
    )
    {
        fun toResponse() = PlayerResponse(
            pseudo = pseudo,
            score = score,
            rank = rank
        )
    }
