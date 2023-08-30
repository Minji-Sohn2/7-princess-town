package com.example.princesstown.dto.chatRoom;

import lombok.Getter;

import java.util.List;

@Getter
public class MyChatRoomResponseDto {
    private List<ChatRoomInfoResponseDto> myChatRoomList;

    public MyChatRoomResponseDto(List<ChatRoomInfoResponseDto> myChatRoomList) {
        this.myChatRoomList = myChatRoomList;
    }
}
