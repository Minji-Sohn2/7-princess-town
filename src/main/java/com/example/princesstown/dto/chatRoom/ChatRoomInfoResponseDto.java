package com.example.princesstown.dto.chatRoom;

import com.example.princesstown.entity.ChatRoom;
import lombok.Getter;

@Getter
public class ChatRoomInfoResponseDto {
    private String chatRoomId;
    private String chatRoomName;
    private Long hostUserId;
    private int memberCount;


    public ChatRoomInfoResponseDto(ChatRoom chatRoom) {
        this.chatRoomId = chatRoom.getId();
        this.chatRoomName = chatRoom.getChatRoomName();
        this.hostUserId = chatRoom.getHostUser().getUserId();
        this.memberCount = chatRoom.getChatUserList().size();
    }
}
