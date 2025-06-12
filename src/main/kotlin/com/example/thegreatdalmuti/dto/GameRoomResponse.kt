package com.example.thegreatdalmuti.dto

import com.example.thegreatdalmuti.domain.GameRoom
import com.example.thegreatdalmuti.type.GameState

data class GameRoomResponse(
    val id: Long,
    val name: String,
    val playerCount: Int,
    val maxPlayers: Int = 7,
    val gameState: GameState,
    val canJoin: Boolean
) {
    companion object {
        fun from(gameRoom: GameRoom): GameRoomResponse {
            return GameRoomResponse(
                id = gameRoom.id,
                name = gameRoom.name,
                playerCount = gameRoom.players.size,
                gameState = gameRoom.gameState,
                canJoin = gameRoom.players.size < 7 && gameRoom.gameState == GameState.WAITING
            )
        }
    }
}
