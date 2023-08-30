package com.example.princesstown.dto.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Builder
    public ChatMessageDto(MessageType type, Long roomId, String senderNickname, String message) {
        this.type = type;
        this.roomId = roomId;
        this.sender = senderNickname;
        this.message = message;
    }
}
