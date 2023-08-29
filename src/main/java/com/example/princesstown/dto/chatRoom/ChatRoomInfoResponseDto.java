package com.example.princesstown.dto.chatRoom;

import com.example.princesstown.entity.ChatRoom;
import lombok.Getter;

@Getter
public class ChatRoomInfoResponseDto {
    private Long jpaSavedId;
    private String chatRoomId;
    private String chatRoomName;
    private Long hostUserId;


    public ChatRoomInfoResponseDto(ChatRoom chatRoom) {
        this.jpaSavedId = chatRoom.getId();
        this.chatRoomId = chatRoom.getChatRoomId();
        this.chatRoomName = chatRoom.getChatRoomName();
        this.hostUserId = chatRoom.getHostUserId();
    }
}
