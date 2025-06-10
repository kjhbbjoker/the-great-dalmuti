package com.example.thegreatdalmuti.type

/**
 * 웹소켓 메시지 타입 enum
 */
enum class WebSocketMessageType(val value: String) {
    // 플레이어 관련
    PLAYER_CONNECT("PLAYER_CONNECT"),
    PLAYER_CONNECTED("PLAYER_CONNECTED"),
    PLAYER_DISCONNECT("PLAYER_DISCONNECT"),
    PLAYER_DISCONNECTED("PLAYER_DISCONNECTED"),
    PLAYER_JOINED("PLAYER_JOINED"),
    PLAYER_LEFT("PLAYER_LEFT"),

    // 연결 관련
    CONNECTION_ESTABLISHED("CONNECTION_ESTABLISHED"),
    PING("PING"),
    PONG("PONG"),

    // 게임 룸 관련
    JOIN_ROOM("JOIN_ROOM"),
    ROOM_JOINED("ROOM_JOINED"),
    LEAVE_ROOM("LEAVE_ROOM"),
    ROOM_LEFT("ROOM_LEFT"),
    CREATE_ROOM("CREATE_ROOM"),
    ROOM_CREATED("ROOM_CREATED"),

    // 게임 관련
    GAME_START("GAME_START"),
    GAME_STARTED("GAME_STARTED"),
    GAME_END("GAME_END"),
    GAME_ENDED("GAME_ENDED"),
    CARD_PLAYED("CARD_PLAYED"),
    CARDS_PLAYED("CARDS_PLAYED"),
    TURN_CHANGED("TURN_CHANGED"),
    PLAYER_PASSED("PLAYER_PASSED"),
    PLAYER_FINISHED("PLAYER_FINISHED"),

    // 공통
    ERROR("ERROR"),
    SUCCESS("SUCCESS");

    companion object {
        /**
         * 문자열로부터 enum 값 찾기
         */
        fun fromString(value: String): WebSocketMessageType? {
            return entries.find { it.value == value }
        }
    }
}