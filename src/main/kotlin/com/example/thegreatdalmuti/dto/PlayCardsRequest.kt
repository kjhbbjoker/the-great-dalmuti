package com.example.thegreatdalmuti.dto

data class PlayCardsRequest(
    val roomId: Long,
    val cardRanks: List<Int> // 플레이할 카드들의 랭크 번호
)

data class CreateRoomRequest(
    val roomName: String
)

data class JoinRoomRequest(
    val roomId: Long
)
