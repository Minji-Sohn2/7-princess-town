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
    private String sender;  // nickname
    private String message;
    private String imgData;

    @JsonFormat(pattern = "yy-MM-dd HH:mm")
    private String createdAt;

    public ChatMessageDto(ChatMessage chatMessage) {
        this.type = MessageType.TALK;
        this.roomId = chatMessage.getChatRoomId();
        this.sender = chatMessage.getSenderNickname();
        if(chatMessage.getMessage() != null) {
            this.message = chatMessage.getMessage();
        }
        if(chatMessage.getImgUrl() != null) {
            this.imgData = chatMessage.getImgUrl();
        }
        this.createdAt = chatMessage.getCreatedAt().format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm"));
    }

    public ChatMessageDto(Long roomId, String imageUrl) {
        this.type = MessageType.TALK;
        this.roomId = roomId;
        this.imgData = imageUrl;
    }
}
