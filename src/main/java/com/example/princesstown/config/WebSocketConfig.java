package com.example.princesstown.config;

import com.example.princesstown.service.chat.StompHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker   // 문자 채팅
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    // pub/sub 메세징 구현
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub");  // 메세지 받을 때
        config.setApplicationDestinationPrefixes("/pub");   // 메세지 보낼 때
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp")   // Stomp websocket handshake 경로 설정
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    /*
        Websocket 앞단에서 StompHandler가 token을 체크
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        //필터체인 통과 후 컨트롤러에 가기 전 Message 객체를 가져오기
        registration.interceptors(stompHandler);
    }
}
