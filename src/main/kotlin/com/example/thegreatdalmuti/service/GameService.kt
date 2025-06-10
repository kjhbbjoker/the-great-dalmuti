package com.example.thegreatdalmuti.service

import com.example.thegreatdalmuti.domain.Card
import com.example.thegreatdalmuti.domain.GameRoom
import com.example.thegreatdalmuti.domain.PlayerDomain
import com.example.thegreatdalmuti.type.GameResult
import com.example.thegreatdalmuti.type.GameState
import org.springframework.stereotype.Service

@Service
class GameService(
    private val cardService: CardService
) {

    // 메모리에 게임 룸들을 저장 (실제로는 Redis나 DB 사용 권장)
    private val gameRooms = mutableMapOf<Long, GameRoom>()
    private var nextRoomId = 1L

    /**
     * 새 게임 룸 생성
     */
    fun createRoom(roomName: String, creator: PlayerDomain): GameRoom {
        val room = GameRoom(
            id = nextRoomId++,
            name = roomName
        )
        room.addPlayer(creator)
        gameRooms[room.id] = room
        return room
    }

    /**
     * 게임 룸 조회
     */
    fun findRoom(roomId: Long): GameRoom {
        return gameRooms[roomId] ?: throw IllegalArgumentException("존재하지 않는 방입니다.")
    }

    /**
     * 게임 룸 목록 조회
     */
    fun getAllRooms(): List<GameRoom> {
        return gameRooms.values.toList()
    }

    /**
     * 플레이어가 방에 입장
     */
    fun joinRoom(roomId: Long, player: PlayerDomain): GameRoom {
        val room = findRoom(roomId)
        room.addPlayer(player)
        return room
    }

    /**
     * 플레이어가 방에서 퇴장
     */
    fun leaveRoom(roomId: Long, player: PlayerDomain): GameRoom {
        val room = findRoom(roomId)
        room.removePlayer(player)
        return room
    }

    /**
     * 게임 시작
     */
    fun startGame(roomId: Long): GameRoom {
        val room = findRoom(roomId)
        require(room.canStartGame()) { "게임을 시작할 수 없습니다. (최소 3명 필요)" }

        // 카드 덱 생성 및 배분
        val deck = cardService.createShuffledDeck()
        cardService.distributeCards(room.players, deck)

        // 게임 상태 변경
        room.gameState = GameState.PLAYING

        // 첫 번째 플레이어 결정 (1번 카드를 가진 플레이어)
        room.currentTurnPlayer = determineFirstPlayer(room.players)

        return room
    }

    /**
     * 첫 번째 플레이어 결정
     * 1번 카드(DALMUTI)를 가진 플레이어가 먼저 시작
     */
    private fun determineFirstPlayer(players: List<PlayerDomain>): PlayerDomain {
        return players.find { player ->
            player.cards.any { it.rank.rankNumber == 1 }
        } ?: players.first() // 1번 카드가 없으면 첫 번째 플레이어
    }

    /**
     * 카드 내기
     */
    fun playCards(roomId: Long, playerId: Long, cards: List<Card>): GameResult {
        val room = findRoom(roomId)
        require(room.gameState == GameState.PLAYING) { "게임이 진행 중이 아닙니다." }

        val currentPlayer = room.currentTurnPlayer
            ?: throw IllegalStateException("현재 턴 플레이어가 설정되지 않았습니다.")

        require(currentPlayer.id == playerId) { "현재 턴이 아닙니다." }

        // 카드 유효성 검증
        require(cardService.isValidCardCombination(cards)) { "유효하지 않은 카드 조합입니다." }
        require(cardService.canPlayCards(cards, room.currentFieldCards)) { "현재 필드보다 강한 카드를 내야 합니다." }

        // 플레이어가 해당 카드들을 가지고 있는지 확인
        require(currentPlayer.cards.containsAll(cards)) { "가지고 있지 않은 카드입니다." }

        // 카드 제거
        currentPlayer.cards.removeAll(cards)

        // 필드에 카드 추가
        room.currentFieldCards = cards.toMutableList()
        room.lastPlayedPlayer = currentPlayer

        // 게임 종료 확인
        if (currentPlayer.cards.isEmpty()) {
            return handlePlayerFinished(room, currentPlayer)
        }

        // 다음 플레이어로 턴 넘기기
        room.currentTurnPlayer = getNextPlayer(room, currentPlayer)

        return GameResult.CONTINUE
    }

    /**
     * 플레이어가 모든 카드를 다 냈을 때 처리
     */
    private fun handlePlayerFinished(room: GameRoom, player: PlayerDomain): GameResult {
        room.finishedPlayers.add(player)

        // 게임 종료 확인 (마지막 한 명 남을 때까지)
        if (room.players.size - room.finishedPlayers.size <= 1) {
            room.gameState = GameState.FINISHED
            return GameResult.GAME_FINISHED
        }

        // 다음 플레이어로 턴 넘기기
        room.currentTurnPlayer = getNextActivePlayer(room, player)
        return GameResult.PLAYER_FINISHED
    }

    /**
     * 다음 플레이어 가져오기
     */
    private fun getNextPlayer(room: GameRoom, currentPlayer: PlayerDomain): PlayerDomain {
        val currentIndex = room.players.indexOf(currentPlayer)
        val nextIndex = (currentIndex + 1) % room.players.size
        return room.players[nextIndex]
    }

    /**
     * 다음 활성 플레이어 가져오기 (게임이 끝나지 않은 플레이어)
     */
    private fun getNextActivePlayer(room: GameRoom, currentPlayer: PlayerDomain): PlayerDomain? {
        val activePlayers = room.players.filter { it !in room.finishedPlayers }
        if (activePlayers.size <= 1) return null

        val currentIndex = room.players.indexOf(currentPlayer)
        var nextIndex = (currentIndex + 1) % room.players.size

        while (room.players[nextIndex] in room.finishedPlayers) {
            nextIndex = (nextIndex + 1) % room.players.size
        }

        return room.players[nextIndex]
    }

    /**
     * 패스하기
     */
    fun passPlayer(roomId: Long, playerId: Long): GameRoom {
        val room = findRoom(roomId)
        require(room.gameState == GameState.PLAYING) { "게임이 진행 중이 아닙니다." }

        val currentPlayer = room.currentTurnPlayer
            ?: throw IllegalStateException("현재 턴 플레이어가 설정되지 않았습니다.")

        require(currentPlayer.id == playerId) { "현재 턴이 아닙니다." }

        // 다음 플레이어로 턴 넘기기
        room.currentTurnPlayer = getNextPlayer(room, currentPlayer)

        return room
    }
}


