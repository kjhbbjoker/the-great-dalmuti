package com.example.thegreatdalmuti.config

import com.example.thegreatdalmuti.handler.SocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val gameWebSocketHandler: SocketHandler
) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(gameWebSocketHandler, "/ws/game")
            .setAllowedOrigins("*") // 프로덕션에서는 특정 도메인으로 제한
            // SockJS 지원 (WebSocket을 지원하지 않는 브라우저 대응)


    }
}