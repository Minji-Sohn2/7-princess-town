package com.example.princesstown.chat.dto;

import com.example.princesstown.chat.entity.ChatRoom;
import lombok.Getter;

@Getter
public class ChatRoomInfoResponseDto {
    private Long chatRoomId;
    private String chatRoomName;
    private Long hostUserId;


    public ChatRoomInfoResponseDto(ChatRoom chatRoom) {
        this.chatRoomId = chatRoom.getId();
        this.chatRoomName = chatRoom.getChatRoomName();
        this.hostUserId = chatRoom.getHostUserId();
    }
}
