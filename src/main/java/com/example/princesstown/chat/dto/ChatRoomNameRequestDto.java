package com.example.princesstown.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomNameRequestDto {
    private String newChatRoomName;

    public ChatRoomNameRequestDto(String newChatRoomName) {
        this.newChatRoomName = newChatRoomName;
    }
}
