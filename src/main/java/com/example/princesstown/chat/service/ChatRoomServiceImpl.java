package com.example.princesstown.chat.service;

import com.example.princesstown.chat.dto.ChatMemberIdDto;
import com.example.princesstown.chat.dto.ChatRoomInfoResponseDto;
import com.example.princesstown.chat.dto.MemberIdListDto;
import com.example.princesstown.chat.entity.ChatRoom;
import com.example.princesstown.chat.repository.ChatRoomRepository;
import com.example.princesstown.entity.User;
import com.example.princesstown.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    public ChatRoomInfoResponseDto createChatRoom(User user, MemberIdListDto memberIdListDto) {
        ChatRoom newChatRoom = new ChatRoom();

        // userList에 초대한 사용자와 생성한 사용자 추가
        List<User> userList = dtoToUserList(memberIdListDto);
        userList.add(user);

        // 생성된 채팅방에 user 정보 추가(ChatUser)
        newChatRoom.addChatUser(userList);

        chatRoomRepository.save(newChatRoom);

        return new ChatRoomInfoResponseDto(user, newChatRoom);
    }

    private User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NullPointerException("존재하지 않는 사용자입니다.")
        );
    }

    private List<User> dtoToUserList (MemberIdListDto memberIdListDto) {
        // 전달받은 사용자 리스트
        List<User> userList = new ArrayList<>();
        for(ChatMemberIdDto cmd : memberIdListDto.getMemberIdList()) {
            User user = findById(cmd.getUserId());
            userList.add(user);
        }
        return userList;
    }
}
