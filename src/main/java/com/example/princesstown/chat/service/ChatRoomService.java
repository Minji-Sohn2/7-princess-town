package com.example.princesstown.chat.service;

import com.example.princesstown.chat.dto.ChatRoomInfoResponseDto;
import com.example.princesstown.chat.dto.MemberIdListDto;
import com.example.princesstown.entity.User;

public interface ChatRoomService {
    /**
     * 채팅방 생성
     *
     * @param user            생성하는 user
     * @param memberIdListDto 초대할 userId list
     * @return 생성된 채팅방 정보
     */
    ChatRoomInfoResponseDto createChatRoom(User user, MemberIdListDto memberIdListDto);
}
