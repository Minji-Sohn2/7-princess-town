package com.example.princesstown.dto.chatRoom;

import lombok.Getter;

import java.util.List;

@Getter
public class CreateChatRoomRequestDto {
    private String chatRoomName;
    private List<ChatMemberIdDto> memberIdList;
}
