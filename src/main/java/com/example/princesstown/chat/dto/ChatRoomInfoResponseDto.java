package com.example.princesstown.chat.dto;

import com.example.princesstown.chat.entity.ChatRoom;
import com.example.princesstown.entity.User;
import lombok.Getter;

@Getter
public class ChatRoomInfoResponseDto {
    private Long chatRoomId;
    private String chatRoomName;
    private Long hostUserId;
    private int memberCount;


    public ChatRoomInfoResponseDto(User user, ChatRoom chatRoom) {
        this.chatRoomId = chatRoom.getId();
        this.chatRoomName = chatRoom.getChatRoomName();
        this.hostUserId = user.getId();
        this.memberCount = chatRoom.getChatUserList().size();
    }
}
