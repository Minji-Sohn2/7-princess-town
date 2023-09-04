package com.example.princesstown.dto.chat;

import com.example.princesstown.entity.chat.ChatMessage;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageDto {

    // 메세지 타입 : 입장, 채팅
    public enum MessageType {
        ENTER, TALK, QUIT
    }

    private MessageType type;
    private Long roomId;
    private String sender;  // username
    private String message;

    @JsonFormat(pattern = "yy-MM-dd HH:mm")
    private String createdAt;

    public ChatMessageDto(ChatMessage chatMessage) {
        this.type = MessageType.TALK;
        this.roomId = chatMessage.getChatRoomId();
        this.sender = chatMessage.getSenderNickname();
        this.message = chatMessage.getMessage();
        this.createdAt = chatMessage.getCreatedAt().format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm"));
    }
}
