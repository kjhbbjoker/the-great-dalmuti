package com.example.thegreatdalmuti.service

import com.example.thegreatdalmuti.dto.CreatePlayerResponse
import com.example.thegreatdalmuti.domain.Player
import com.example.thegreatdalmuti.dto.CreatePlayerRequest
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Service
class PlayerService {

    private val activePlayers = ConcurrentHashMap<String, Player>()


    /**
     * 닉네임으로 플레이어 생성/접속
     * - 닉네임 중복 체크 후 플레이어 생성
     */
    fun createOrConnectPlayer(request: CreatePlayerRequest): CreatePlayerResponse {
        // 닉네임 중복 체크
        val existingPlayer = activePlayers.values.find { it.username == request.nickname }
        if (existingPlayer != null) {
            throw IllegalArgumentException("이미 사용 중인 닉네임입니다: ${request.nickname}")
        }

        val playerId = UUID.randomUUID().toString()

        val player = Player(
            id = playerId, // String ID 사용
            username = request.nickname,
        )

        // 메모리에 플레이어 저장
        activePlayers[playerId] = player

        return CreatePlayerResponse(
            playerId = playerId,
            username = player.username,
            isNewPlayer = true,
            message = "${player.username}님 접속 완료!"
        )
    }

    /**
     * 플레이어 연결 해제
     */
    fun disconnectPlayer(playerId: String) {
        activePlayers.remove(playerId)
    }

    /**
     * 플레이어 조회
     */
    fun getPlayer(playerId: String): Player? {
        return activePlayers[playerId]
    }

    /**
     * 모든 활성 플레이어 조회
     */
    fun getAllActivePlayers(): List<Player> {
        return activePlayers.values.toList()
    }

    /**
     * 활성 플레이어 수 조회
     */
    fun getActivePlayerCount(): Int {
        return activePlayers.size
    }

    /**
     * 닉네임으로 플레이어 찾기
     */
    fun findPlayerByUsername(username: String): Player? {
        return activePlayers.values.find { it.username == username }
    }
}