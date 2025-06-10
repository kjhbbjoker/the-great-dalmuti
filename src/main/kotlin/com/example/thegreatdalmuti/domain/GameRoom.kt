package com.example.thegreatdalmuti.domain


import com.example.thegreatdalmuti.type.GameState
import java.time.LocalDateTime

data class GameRoom(
    val id: Long,
    var name: String,                                           // 게임 방 이름
    var players: MutableList<Player> = mutableListOf(),   // 방에 있는 플레이어들 리스트
    var gameState: GameState = GameState.WAITING,               // 게임 상태
    var currentTurnPlayer: Player? = null,                // 현재 턴 플레이어
    var currentFieldCards: MutableList<Card> = mutableListOf(), // 현재 필드에 놓인 카드들
    var lastPlayedPlayer: Player? = null,                 // 마지막으로 카드를 낸 플레이어
    var finishedPlayers: MutableList<Player> = mutableListOf(), // 게임을 마친 플레이어들 (순위)
    val createdAt: LocalDateTime = LocalDateTime.now()          // 생성 시각
) {
    /**
     * 플레이어 추가
     */
    fun addPlayer(player: Player) {
        require(players.size < 7) { "방에 더 이상 플레이어를 추가할 수 없습니다." }
        require(gameState == GameState.PLAYING) { "게임이 이미 시작되었습니다." }
        players.add(player)
    }

    /**
     * 플레이어 제거
     */
    fun removePlayer(player: Player) {
        players.remove(player)
        // 게임 진행 중인데 플레이어가 나가면 게임 상태 처리 필요
        if (gameState == GameState.PLAYING && players.size < 3) {
            gameState = GameState.WAITING
        }
    }

    /**
     * 게임 시작 가능 여부 확인
     */
    fun canStartGame(): Boolean {
        return players.size >= 3 && gameState == GameState.WAITING
    }

    /**
     * 현재 활성 플레이어 수 (게임이 끝나지 않은 플레이어)
     */
    fun getActivePlayerCount(): Int {
        return players.size - finishedPlayers.size
    }

    /**
     * 필드 카드 초기화 (새 라운드 시작 시)
     */
    fun clearField() {
        currentFieldCards.clear()
        lastPlayedPlayer = null
    }
}