package com.example.princesstown.chat.service;

import com.example.princesstown.chat.dto.*;
import com.example.princesstown.chat.entity.ChatRoom;
import com.example.princesstown.chat.entity.ChatUser;
import com.example.princesstown.chat.repository.ChatRoomRepository;
import com.example.princesstown.chat.repository.ChatUserRepository;
import com.example.princesstown.entity.User;
import com.example.princesstown.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j(topic = "ChatRoomService")
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatUserRepository chatUserRepository;

    @Override
    @Transactional
    public ChatRoomInfoResponseDto createChatRoom(User user, MemberIdListDto memberIdListDto) {
        ChatRoom newChatRoom = new ChatRoom(user);

        // userList에 초대한 사용자와 생성한 사용자 추가
        List<User> userList = dtoToUserList(memberIdListDto);
        userList.add(user);

        for (User u : userList) {
            chatUserRepository.save(new ChatUser(u, newChatRoom));
        }

        chatRoomRepository.save(newChatRoom);

        return new ChatRoomInfoResponseDto(newChatRoom);
    }

    @Override
    @Transactional
    public ChatRoomInfoResponseDto updateChatRoomName(Long chatRoomId, User user, ChatRoomNameRequestDto requestDto) {
        // 요청한 user가 채팅방의 생성자인지 확인
        ChatRoom chatRoom = findChatRoomById(chatRoomId);

        if (!chatRoom.getHostUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("채팅방의 이름은 호스트만 변경할 수 있습니다.");
        }

        chatRoom.updateChatRoomName(requestDto.getNewChatRoomName());

        return new ChatRoomInfoResponseDto(chatRoom);
    }

    @Override
    public void deleteChatRoom(Long chatRoomId, User user) {
        // 요청한 user가 채팅방의 생성자인지 확인
        ChatRoom chatRoom = findChatRoomById(chatRoomId);

        if (!chatRoom.getHostUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("채팅방은 호스트만 삭제할 수 있습니다.");
        }

        chatRoomRepository.delete(chatRoom);
    }

    @Override
    public MemberIdListDto getChatRoomMembers(Long chatRoomId) {

        List<ChatMemberIdDto> chatMemberIdList = chatUserRepository.findAllByChatRoom_Id(chatRoomId)
                .stream().map(ChatMemberIdDto::new).toList();

        return new MemberIdListDto(chatMemberIdList);
    }

    @Override
    public MyChatRoomResponseDto getMyChatRooms(User user) {
        List<ChatRoomInfoResponseDto> myChatRoomList = chatUserRepository.findAllByUser(user)
                .stream()
                .map(ChatUser::getChatRoom)
                .map(ChatRoomInfoResponseDto::new).toList();
        return new MyChatRoomResponseDto(myChatRoomList);
    }

    @Override
    public void leaveChatRoom(Long roomId, User user) {
        ChatRoom chatRoom = findChatRoomById(roomId);
        ChatUser chatUser = findChatUserByChatRoomAndUser(chatRoom, user);

        if (chatUser == null) {
            throw new IllegalArgumentException("해당 채팅방에 속해있지 않습니다.");
        }

        // user가 해당 채팅방의 host인 경우 채팅방 삭제
        if (chatRoom.getHostUserId().equals(user.getUserId())) {
            deleteChatRoom(roomId, user);
        }

        chatUserRepository.delete(chatUser);
    }

    @Override
    public void inviteMember(Long roomId, MemberIdListDto memberIdListDto, User user) {
        ChatRoom chatRoom = findChatRoomById(roomId);
        ChatUser chatUser = findChatUserByChatRoomAndUser(chatRoom, user);

        if (chatUser == null) {
            throw new IllegalArgumentException("해당 채팅방으로의 초대 권한이 없습니다.");
        }

        List<User> userList = dtoToUserList(memberIdListDto);
        for (User u : userList) {
            // 만약 이미 채팅방에 존재하는 멤버라면 건너뛰기
            if(findChatUserByChatRoomAndUser(chatRoom, u) != null) {
                continue;
            }
            chatUserRepository.save(new ChatUser(u, chatRoom));
        }

    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NullPointerException("존재하지 않는 사용자입니다.")
        );
    }

    @Override
    public ChatRoom findChatRoomById(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new NullPointerException("존재하지 않는 채팅방입니다.")
        );
    }

    @Override
    public ChatUser findChatUserByChatRoomAndUser(ChatRoom chatRoom, User user) {
        return chatUserRepository.findByChatRoomAndUser(chatRoom, user);
    }

    @Override
    public List<User> dtoToUserList(MemberIdListDto memberIdListDto) {
        log.info(memberIdListDto.getMemberIdList().get(0).toString());
        // 전달받은 사용자 리스트
        List<User> userList = new ArrayList<>();
        for (ChatMemberIdDto cmd : memberIdListDto.getMemberIdList()) {
            User user = findUserById(cmd.getUserId());
            userList.add(user);
        }
        return userList;
    }
}
