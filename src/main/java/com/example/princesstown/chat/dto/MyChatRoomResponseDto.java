package com.example.princesstown.chat.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class MyChatRoomResponseDto {
    private List<ChatRoomInfoResponseDto> myChatRoomList;

    public MyChatRoomResponseDto(List<ChatRoomInfoResponseDto> myChatRoomList) {
        this.myChatRoomList = myChatRoomList;
    }
}
