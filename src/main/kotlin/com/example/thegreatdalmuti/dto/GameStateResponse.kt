package com.example.thegreatdalmuti.dto

import com.example.thegreatdalmuti.domain.Card
import com.example.thegreatdalmuti.domain.GameRoom
import com.example.thegreatdalmuti.domain.Player
import com.example.thegreatdalmuti.type.GameState

data class GameStateResponse(
    val roomId: Long,
    val roomName: String,
    val gameState: GameState,
    val players: List<PlayerInGameDto>,
    val currentTurnPlayerId: String?,
    val fieldCards: List<CardDto>,
    val lastPlayedPlayerId: String?,
    val myCards: List<CardDto>?,
    val finishedPlayers: List<PlayerInGameDto>
) {
    companion object {
        fun from(gameRoom: GameRoom, currentPlayer: Player?): GameStateResponse {
            return GameStateResponse(
                roomId = gameRoom.id,
                roomName = gameRoom.name,
                gameState = gameRoom.gameState,
                players = gameRoom.players.map { PlayerInGameDto.from(it) },
                currentTurnPlayerId = gameRoom.currentTurnPlayer?.id,
                fieldCards = gameRoom.currentFieldCards.map { CardDto.from(it) },
                lastPlayedPlayerId = gameRoom.lastPlayedPlayer?.id,
                myCards = currentPlayer?.cards?.map { CardDto.from(it) },
                finishedPlayers = gameRoom.finishedPlayers.map { PlayerInGameDto.from(it) }
            )
        }
    }
}

data class PlayerInGameDto(
    val id: String,
    val username: String,
    val cardCount: Int,
    val isFinished: Boolean
) {
    companion object {
        fun from(player: Player): PlayerInGameDto {
            return PlayerInGameDto(
                id = player.id ?: "",
                username = player.username,
                cardCount = player.cards.size,
                isFinished = false // 나중에 GameRoom에서 확인
            )
        }
    }
}

data class CardDto(
    val name: String,
    val rank: Int
) {
    companion object {
        fun from(card: Card): CardDto {
            return CardDto(
                name = card.name,
                rank = card.rank.rankNumber
            )
        }
    }
}
