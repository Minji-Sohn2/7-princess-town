package com.example.princesstown.service.chat;

import com.example.princesstown.dto.chat.ChatMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j(topic = "RedisSubscriber")
@RequiredArgsConstructor
@Service
public class RedisSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    /*
        Redis에서 메세지가 발행(publish)
        -> 대기하고있던 onMessage() 가 해당 메세지 처리
     */
    public void sendMessage(String publishMessage) {
        try {
            // ChatMessage 객체로 매핑
            ChatMessageDto roomMessage = objectMapper.readValue(publishMessage, ChatMessageDto.class);
            // Websocket 구독자에게 ChatMessage 발송
            messagingTemplate.convertAndSend("/sub/chat/room/" + roomMessage.getRoomId(), roomMessage);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }
}
