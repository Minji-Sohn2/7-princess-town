package com.example.princesstown.service.chat;

import com.example.princesstown.dto.chat.ChatMessageDto;
import com.example.princesstown.entity.User;
import com.example.princesstown.entity.chat.ChatMessage;
import com.example.princesstown.repository.chat.ChatMessageRepository;
import com.example.princesstown.repository.chat.ChatRoomRepository;
import com.example.princesstown.repository.user.UserRepository;
import com.example.princesstown.security.jwt.JwtUtil;
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
public class ChatServiceImpl implements ChatService{

    private final JwtUtil jwtUtil;
    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    public String getRoomId(String destination) {
        log.info("getRoomId 원래 destination -> " + destination);
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            return "";
    }

    /**
     * 채팅방에 메시지 발송
     */
    @Override
    public void sendChatMessage(ChatMessageDto chatMessage, String token) {
        log.info("sendMessage 메서드 시작");

        String username = jwtUtil.getUsernameFromJwt(token);
        log.info("받은 메세지 토큰으로 찾은 username : " + username);
        User user = findUserByUsername(username);

        chatMessage.setSender(user.getNickname());

        if (ChatMessageDto.MessageType.QUIT.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에서 나갔습니다.");
            chatMessage.setSender("[알림]");
        } else {
            chatMessageRepository.save(new ChatMessage(user, chatMessage));
        }

        chatMessage.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm")));
        log.info("메세지 발송 시각? " + chatMessage.getCreatedAt());

        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new NullPointerException("존재하지 않는 사용자"));
    }
}
