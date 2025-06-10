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
     * - 닉네임이 이미 사용 중이면 새로운 UUID 발급
     * - 닉네임이 사용 가능하면 새 플레이어 생성
     */
    fun createOrConnectPlayer(request: CreatePlayerRequest): CreatePlayerResponse {
        val playerId = UUID.randomUUID().toString()

        val player = Player(
            id = null, // 임시 접속이므로 DB ID는 null
            username = request.nickname,
        )

        // 메모리에 플레이어 저장
        activePlayers[playerId] = player

        return CreatePlayerResponse(
            playerId = playerId,
            username = player.username,
            isNewPlayer = true,
            message = "접속 완료"
        )
    }

    /**
     * 플레이어 연결 해제
     */
    fun disconnectPlayer(playerId: String) {
        activePlayers.remove(playerId)
    }
}