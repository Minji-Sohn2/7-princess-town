package com.example.princesstown.chat.service;

import com.example.princesstown.chat.dto.ChatRoomInfoResponseDto;
import com.example.princesstown.chat.dto.ChatRoomNameRequestDto;
import com.example.princesstown.chat.dto.MemberIdListDto;
import com.example.princesstown.chat.dto.MyChatRoomResponseDto;
import com.example.princesstown.entity.User;

import java.util.List;

public interface ChatRoomService {
    /**
     * 채팅방 생성
     *
     * @param user            생성하는 user
     * @param memberIdListDto 초대할 userId list
     * @return 생성된 채팅방 정보
     */
    ChatRoomInfoResponseDto createChatRoom(User user, MemberIdListDto memberIdListDto);

    /**
     * 채팅방 이름 수정
     *
     * @param chatRoomId 이름을 수정할 채팅방
     * @param user       요청하는 user
     * @param requestDto 새로운 채팅방 이름
     * @return 수정된 채팅방 정보
     */
    ChatRoomInfoResponseDto updateChatRoomName(Long chatRoomId, User user, ChatRoomNameRequestDto requestDto);

    /**
     * 채팅방 삭제
     *
     * @param roomId 삭제할 채팅방 id
     * @param user   요청하는 user
     */
    void deleteChatRoom(Long roomId, User user);

    /**
     * 채팅방 멤버 조회
     *
     * @param roomId 조회할 채팅방 id
     * @return 멤버 id list
     */
    MemberIdListDto getChatRoomMembers(Long roomId);

    /**
     * 내가 속한 채팅방 조회
     *
     * @param user 요청한 user
     * @return 내가 속한 채팅방 list
     */
    MyChatRoomResponseDto getMyChatRooms(User user);

    /**
     * 채팅방 나가기
     *
     * @param roomId 나갈 채팅방 id
     * @param user   요청한 user
     */
    void leaveChatRoom(Long roomId, User user);

    /**
     * userId로 user 찾기
     *
     * @param userId 찾을 userId
     * @return 찾은 user
     */
    User findUserById(Long userId);

    /**
     * 초대할 userId list를 user list로 변환
     *
     * @param memberIdListDto userId list
     * @return user list
     */
    List<User> dtoToUserList(MemberIdListDto memberIdListDto);
}
