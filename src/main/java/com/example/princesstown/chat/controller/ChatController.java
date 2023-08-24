package com.example.princesstown.chat.controller;

import com.example.princesstown.chat.dto.ChatMessage;
import com.example.princesstown.chat.service.ChatService;
import com.example.princesstown.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

/*
    publisher 구현 (WebSocketChatHandler -> ChatController)
 */
@RequiredArgsConstructor
@Controller
@Slf4j(topic = "ChatController")
public class ChatController {

    private final JwtUtil jwtUtil;
    private final ChatService ChatService;

    /*
        Websocket "/pub/chat/message"로 들어오는 메세지 처리
     */
    @MessageMapping("/chat/message")
    public void message(ChatMessage message, @Header("Authorization") String token) {
        log.info("ChatController - message 전송");

        String username = jwtUtil.getUsernameFromJwt(token);
        log.info("받은 메세지 토큰으로 찾은 username : " + username);

        // 로그인 회원 정보로 대화명 설정
        message.setSender(username);

        // Websocket 에 발행된 메세지 redis 로 발행 (publish)
        ChatService.sendChatMessage(message);
    }
}
