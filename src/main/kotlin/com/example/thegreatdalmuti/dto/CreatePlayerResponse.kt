package com.example.thegreatdalmuti.dto

data class CreatePlayerResponse(
    val playerId: String,
    val username: String,
    val isNewPlayer: Boolean,
    val message: String
)