package com.example.thegreatdalmuti.controller

import com.example.thegreatdalmuti.domain.Card
import com.example.thegreatdalmuti.dto.*
import com.example.thegreatdalmuti.service.GameService
import com.example.thegreatdalmuti.service.PlayerService
import com.example.thegreatdalmuti.type.RankType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = ["*"])
class GameController(
    private val gameService: GameService,
    private val playerService: PlayerService
) {

    /**
     * 게임 룸 목록 조회
     */
    @GetMapping("/rooms")
    fun getAllRooms(): ResponseEntity<List<GameRoomResponse>> {
        val rooms = gameService.getAllRooms()
        val responses = rooms.map { GameRoomResponse.from(it) }
        return ResponseEntity.ok(responses)
    }

    /**
     * 게임 룸 생성
     */
    @PostMapping("/rooms")
    fun createRoom(
        @RequestBody request: CreateRoomRequest,
        @RequestHeader("Player-Id") playerId: String
    ): ResponseEntity<GameRoomResponse> {
        println("debug: $playerId")
        val player = playerService.getPlayer(playerId)
            ?: return ResponseEntity.badRequest().build()

        val room = gameService.createRoom(request.roomName, player)
        return ResponseEntity.ok(GameRoomResponse.from(room))
    }

    /**
     * 게임 룸 입장
     */
    @PostMapping("/rooms/{roomId}/join")
    fun joinRoom(
        @PathVariable roomId: Long,
        @RequestHeader("Player-Id") playerId: String
    ): ResponseEntity<GameRoomResponse> {
        return try {
            val player = playerService.getPlayer(playerId)
                ?: return ResponseEntity.badRequest().build()

            val room = gameService.joinRoom(roomId, player)
            ResponseEntity.ok(GameRoomResponse.from(room))
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * 게임 룸 퇴장
     */
    @PostMapping("/rooms/{roomId}/leave")
    fun leaveRoom(
        @PathVariable roomId: Long,
        @RequestHeader("Player-Id") playerId: String
    ): ResponseEntity<GameRoomResponse> {
        return try {
            val player = playerService.getPlayer(playerId)
                ?: return ResponseEntity.badRequest().build()

            val room = gameService.leaveRoom(roomId, player)
            ResponseEntity.ok(GameRoomResponse.from(room))
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * 게임 시작
     */
    @PostMapping("/rooms/{roomId}/start")
    fun startGame(
        @PathVariable roomId: Long,
        @RequestHeader("Player-Id") playerId: String
    ): ResponseEntity<GameStateResponse> {
        return try {
            val player = playerService.getPlayer(playerId)
                ?: return ResponseEntity.badRequest().build()

            val room = gameService.startGame(roomId)
            ResponseEntity.ok(GameStateResponse.from(room, player))
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * 현재 게임 상태 조회
     */
    @GetMapping("/rooms/{roomId}/state")
    fun getGameState(
        @PathVariable roomId: Long,
        @RequestHeader("Player-Id") playerId: String
    ): ResponseEntity<GameStateResponse> {
        return try {
            val room = gameService.findRoom(roomId)
            val player = playerService.getPlayer(playerId)
            ResponseEntity.ok(GameStateResponse.from(room, player))
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * 카드 내기
     */
    @PostMapping("/rooms/{roomId}/play")
    fun playCards(
        @PathVariable roomId: Long,
        @RequestBody request: PlayCardsRequest,
        @RequestHeader("Player-Id") playerId: String
    ): ResponseEntity<GameStateResponse> {
        return try {
            val player = playerService.getPlayer(playerId)
                ?: return ResponseEntity.badRequest().build()

            // 랭크 번호를 Card 객체로 변환
            val cards = request.cardRanks.map { rank ->
                val rankType = RankType.entries.find { it.rankNumber == rank }
                    ?: throw IllegalArgumentException("잘못된 카드 랭크: $rank")
                Card(name = rank.toString(), rank = rankType)
            }

            gameService.playCards(roomId, player.id!!, cards)
            val room = gameService.findRoom(roomId)
            
            ResponseEntity.ok(GameStateResponse.from(room, player))
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * 패스하기
     */
    @PostMapping("/rooms/{roomId}/pass")
    fun passPlayer(
        @PathVariable roomId: Long,
        @RequestHeader("Player-Id") playerId: String
    ): ResponseEntity<GameStateResponse> {
        return try {
            val player = playerService.getPlayer(playerId)
                ?: return ResponseEntity.badRequest().build()

            val room = gameService.passPlayer(roomId, player.id!!)
            ResponseEntity.ok(GameStateResponse.from(room, player))
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }
}
