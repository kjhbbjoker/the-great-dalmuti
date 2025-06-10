package com.example.thegreatdalmuti.handler


import com.example.thegreatdalmuti.dto.CreatePlayerRequest
import com.example.thegreatdalmuti.service.PlayerService
import com.example.thegreatdalmuti.type.WebSocketMessageType
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap

@Component
class SocketHandler(
    private val playerService: PlayerService,
    private val objectMapper: ObjectMapper
) : TextWebSocketHandler() {

    // 웹소켓 세션과 플레이어 ID 매핑
    private val sessionToPlayerId = ConcurrentHashMap<String, String>()
    private val playerIdToSession = ConcurrentHashMap<String, WebSocketSession>()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        println("새로운 클라이언트 연결: ${session.id}")

        // 연결 성공 메시지 전송
        sendMessage(
            session, WebSocketMessage(
                type = WebSocketMessageType.CONNECTION_ESTABLISHED,
                data = mapOf("sessionId" to session.id)
            )
        )
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        try {
            val messageContent = message.payload
            println("받은 메시지: $messageContent")

            val wsMessage = objectMapper.readValue(messageContent, WebSocketMessage::class.java)

            when (wsMessage.type) {
                WebSocketMessageType.PLAYER_CONNECT -> handlePlayerConnect(session, wsMessage)
                WebSocketMessageType.PLAYER_DISCONNECT -> handlePlayerDisconnect(session, wsMessage)
                WebSocketMessageType.PING -> handlePing(session)
                WebSocketMessageType.JOIN_ROOM -> handleJoinRoom(session, wsMessage)
                WebSocketMessageType.LEAVE_ROOM -> handleLeaveRoom(session, wsMessage)
                else -> {
                    sendErrorMessage(session, "알 수 없는 메시지 타입: ${wsMessage.type}")
                }
            }
        } catch (e: Exception) {
            println("메시지 처리 중 오류: ${e.message}")
            sendErrorMessage(session, "메시지 처리 중 오류가 발생했습니다.")
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        println("클라이언트 연결 종료: ${session.id}, 상태: ${status.code}")

        // 세션과 연결된 플레이어 정리
        val playerId = sessionToPlayerId.remove(session.id)
        if (playerId != null) {
            playerIdToSession.remove(playerId)
            // 플레이어 서비스에서 제거
            playerService.disconnectPlayer(playerId)

            // 다른 플레이어들에게 알림
            broadcastPlayerDisconnected(playerId)
        }
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        println("WebSocket 에러: ${exception.message}")
        sendErrorMessage(session, "연결 오류가 발생했습니다.")
    }

    // 플레이어 접속 처리
    private fun handlePlayerConnect(session: WebSocketSession, message: WebSocketMessage) {
        try {
            val data = message.data as Map<*, *>
            val nickname = data["nickname"] as? String
                ?: throw IllegalArgumentException("닉네임이 필요합니다.")

            val request = CreatePlayerRequest(nickname)
            val response = playerService.createOrConnectPlayer(request)

            // 세션과 플레이어 ID 매핑 저장
            sessionToPlayerId[session.id] = response.playerId
            playerIdToSession[response.playerId] = session

            // 접속 성공 응답
            sendMessage(
                session, WebSocketMessage(
                    type = WebSocketMessageType.PLAYER_CONNECTED,
                    data = mapOf(
                        "playerId" to response.playerId,
                        "username" to response.username,
                        "message" to response.message
                    )
                )
            )

            // 다른 플레이어들에게 새 플레이어 접속 알림
            broadcastPlayerConnected(response.playerId, response.username)

        } catch (e: Exception) {
            sendErrorMessage(session, "접속 실패: ${e.message}")
        }
    }

    // 플레이어 접속 해제 처리
    private fun handlePlayerDisconnect(session: WebSocketSession, message: WebSocketMessage) {
        val playerId = sessionToPlayerId[session.id]
        if (playerId != null) {
            sessionToPlayerId.remove(session.id)
            playerIdToSession.remove(playerId)
            playerService.disconnectPlayer(playerId)

            sendMessage(
                session, WebSocketMessage(
                    type = WebSocketMessageType.PLAYER_DISCONNECTED,
                    data = mapOf("message" to "정상적으로 접속 해제되었습니다.")
                )
            )

            broadcastPlayerDisconnected(playerId)
        }
    }

    // 핑 처리 (연결 상태 확인)
    private fun handlePing(session: WebSocketSession) {
        sendMessage(
            session, WebSocketMessage(
                type = WebSocketMessageType.PONG,
                data = mapOf("timestamp" to System.currentTimeMillis())
            )
        )
    }

    // 게임 룸 입장 처리
    private fun handleJoinRoom(session: WebSocketSession, message: WebSocketMessage) {
        // 향후 GameService와 연동
        val data = message.data as Map<*, *>
        val roomId = data["roomId"] as? Long

        sendMessage(
            session, WebSocketMessage(
                type = WebSocketMessageType.ROOM_JOINED,
                data = mapOf("roomId" to roomId)
            )
        )
    }

    // 게임 룸 퇴장 처리
    private fun handleLeaveRoom(session: WebSocketSession, message: WebSocketMessage) {
        // 향후 GameService와 연동
        sendMessage(
            session, WebSocketMessage(
                type = WebSocketMessageType.ROOM_LEFT,
                data = mapOf("message" to "방에서 나갔습니다.")
            )
        )
    }

    // 특정 플레이어에게 메시지 전송
    fun sendMessageToPlayer(playerId: String, message: WebSocketMessage) {
        val session = playerIdToSession[playerId]
        if (session != null && session.isOpen) {
            sendMessage(session, message)
        }
    }

    // 모든 접속자에게 브로드캐스트
    private fun broadcastToAll(message: WebSocketMessage) {
        playerIdToSession.values.forEach { session ->
            if (session.isOpen) {
                sendMessage(session, message)
            }
        }
    }

    // 새 플레이어 접속 알림
    private fun broadcastPlayerConnected(playerId: String, username: String) {
        val message = WebSocketMessage(
            type = WebSocketMessageType.PLAYER_JOINED,
            data = mapOf(
                "playerId" to playerId,
                "username" to username,
                "message" to "$username 님이 접속했습니다."
            )
        )
        broadcastToAll(message)
    }

    // 플레이어 접속 해제 알림
    private fun broadcastPlayerDisconnected(playerId: String) {
        val message = WebSocketMessage(
            type = WebSocketMessageType.PLAYER_LEFT,
            data = mapOf(
                "playerId" to playerId,
                "message" to "플레이어가 접속을 해제했습니다."
            )
        )
        broadcastToAll(message)
    }

    // 세션에 메시지 전송
    private fun sendMessage(session: WebSocketSession, message: WebSocketMessage) {
        try {
            if (session.isOpen) {
                val jsonMessage = objectMapper.writeValueAsString(message)
                session.sendMessage(TextMessage(jsonMessage))
            }
        } catch (e: Exception) {
            println("메시지 전송 실패: ${e.message}")
        }
    }

    // 에러 메시지 전송
    private fun sendErrorMessage(session: WebSocketSession, errorMessage: String) {
        sendMessage(
            session, WebSocketMessage(
                type = WebSocketMessageType.ERROR,
                data = mapOf("message" to errorMessage)
            )
        )
    }

    // 현재 접속자 수 조회
    fun getActiveSessionCount(): Int {
        return sessionToPlayerId.size
    }

    // 플레이어 ID로 세션 존재 여부 확인
    fun isPlayerOnline(playerId: String): Boolean {
        return playerIdToSession.containsKey(playerId)
    }
}


// 웹소켓 메시지 데이터 클래스
data class WebSocketMessage(
    val type: WebSocketMessageType,
    val data: Any? = null,
    val timestamp: Long = System.currentTimeMillis()
)