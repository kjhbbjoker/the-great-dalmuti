package com.example.thegreatdalmuti.domain

import java.time.LocalDateTime

data class GameRoom(
    val id: Long,
    var name: String,                             // 게임 방 이름
    var players: MutableList<PlayerDomain> = mutableListOf(), // 방에 있는 플레이어들 리스트
    val createdAt: LocalDateTime = LocalDateTime.now() // 생성 시각
) {
    /**
     * 플레이어 추가
     */
    fun addPlayer(player: PlayerDomain) {
        require(players.size < 7) { "방에 더 이상 플레이어를 추가할 수 없습니다." }
        players.add(player)
    }

    /**
     * 플레이어 제거
     */
    fun removePlayer(player: PlayerDomain) {
        players.remove(player)
    }
}