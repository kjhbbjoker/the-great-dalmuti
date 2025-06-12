package com.example.thegreatdalmuti.controller

import com.example.thegreatdalmuti.domain.Player
import com.example.thegreatdalmuti.service.PlayerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/players")
@CrossOrigin(origins = ["*"])
class PlayerController(
    private val playerService: PlayerService
) {

    /**
     * 모든 활성 플레이어 조회
     */
    @GetMapping
    fun getAllActivePlayers(): ResponseEntity<List<PlayerInfoResponse>> {
        val players = playerService.getAllActivePlayers()
        val responses = players.map { 
            PlayerInfoResponse(
                username = it.username,
                cardCount = it.cards.size,
                gameRoomId = it.gameRoom?.id
            )
        }
        return ResponseEntity.ok(responses)
    }

    /**
     * 활성 플레이어 수 조회
     */
    @GetMapping("/count")
    fun getActivePlayerCount(): ResponseEntity<Map<String, Int>> {
        val count = playerService.getActivePlayerCount()
        return ResponseEntity.ok(mapOf("activePlayerCount" to count))
    }

    /**
     * 특정 플레이어 정보 조회
     */
    @GetMapping("/{playerId}")
    fun getPlayer(@PathVariable playerId: String): ResponseEntity<PlayerInfoResponse> {
        val player = playerService.getPlayer(playerId)
            ?: return ResponseEntity.notFound().build()

        val response = PlayerInfoResponse(
            username = player.username,
            cardCount = player.cards.size,
            gameRoomId = player.gameRoom?.id
        )
        return ResponseEntity.ok(response)
    }
}

data class PlayerInfoResponse(
    val username: String,
    val cardCount: Int,
    val gameRoomId: Long?
)