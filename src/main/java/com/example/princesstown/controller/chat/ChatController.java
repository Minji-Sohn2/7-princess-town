package com.example.princesstown.controller.chat;

import com.example.princesstown.dto.chat.ChatMessageDto;
import com.example.princesstown.service.chat.ChatService;
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

    private final ChatService ChatService;

    /*
        Websocket "/pub/chat/message"로 들어오는 메세지 처리
     */
    @MessageMapping("/chat/message")
    public void message(ChatMessageDto message, @Header("Authorization") String token) {
        log.info("ChatController - message 전송");

        // Websocket 에 발행된 메세지 redis 로 발행 (publish)
        ChatService.sendChatMessage(message, token);
    }
}
