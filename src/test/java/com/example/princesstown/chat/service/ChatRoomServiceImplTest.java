package com.example.princesstown.chat.service;

import com.example.princesstown.chat.dto.ChatMemberIdDto;
import com.example.princesstown.chat.dto.ChatRoomInfoResponseDto;
import com.example.princesstown.chat.dto.MemberIdListDto;
import com.example.princesstown.chat.entity.ChatRoom;
import com.example.princesstown.chat.repository.ChatRoomRepository;
import com.example.princesstown.entity.User;
import com.example.princesstown.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChatRoomServiceImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatRoomService chatRoomService;

    @DisplayName("채팅방 생성 테스트")
    @Test
    @Transactional
    void createChatRoom() {
        // given
        var loginUser = new User("test1", "1");
        var dto1 = ChatMemberIdDto.builder().userId(1L);
        var dto2 = ChatMemberIdDto.builder().userId(2L);
        var dto3 = ChatMemberIdDto.builder().userId(3L);

        List<ChatMemberIdDto> memberIdList = new ArrayList<>();

        memberIdList.add(dto1.build());
        memberIdList.add(dto2.build());
        memberIdList.add(dto3.build());

        var memberIdListDto = new MemberIdListDto(memberIdList);

        // when
        ChatRoomService chatRoomService = new ChatRoomServiceImpl(userRepository, chatRoomRepository);
        var roomInfo = chatRoomService.createChatRoom(loginUser, memberIdListDto);

        // then
        assert roomInfo.getMemberCount()==4;
    }
}