package com.example.princesstown.chat.service;

import com.example.princesstown.chat.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Service
@Slf4j(topic = "ChatService")
public class ChatService {

    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;

    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            return "";
    }

    /**
     * 채팅방에 메시지 발송
     */
    public void sendChatMessage(ChatMessage chatMessage) {
        log.info("sendMessage 메서드 시작");

        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에 입장했습니다.");
            chatMessage.setSender("[알림]");
        } else if (ChatMessage.MessageType.QUIT.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에서 나갔습니다.");
            chatMessage.setSender("[알림]");
        }
        chatMessage.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm")));
        log.info("메세지 발송 시각? " + chatMessage.getCreatedAt());
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }
}
