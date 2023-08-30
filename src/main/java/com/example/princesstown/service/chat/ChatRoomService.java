package com.example.princesstown.service.chat;

import com.example.princesstown.dto.chatRoom.*;
import com.example.princesstown.entity.User;
import com.example.princesstown.entity.chat.ChatRoom;
import com.example.princesstown.entity.chat.ChatUser;

import java.util.List;

public interface ChatRoomService {
    /**
     * 채팅방 생성
     *
     * @param user       생성하는 user
     * @param requestDto 채팅방 이름, 초대할 userId list
     * @return 생성된 채팅방 정보
     */
    ChatRoomInfoResponseDto createChatRoom(User user, CreateChatRoomRequestDto requestDto);

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
    MemberInfoListDto getChatRoomMembers(Long roomId);

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
     * 채팅방에 user 초대하기
     *
     * @param roomId          초대할 채팅방 id
     * @param memberIdListDto 초대할 user id list
     * @param user            요청한 user
     */
    void inviteMember(Long roomId, MemberIdListDto memberIdListDto, User user);

    /**
     * userId로 user 찾기
     *
     * @param userId 찾을 userId
     * @return 찾은 user
     */
    User findUserById(Long userId);

    /**
     * 채팅방 찾기
     *
     * @param chatRoomId 찾을 채팅방 id
     * @return 찾은 채팅방
     */
    ChatRoom findChatRoomById(Long chatRoomId);

    /**
     * ChatUser 객체 찾기
     *
     * @param chatRoom 찾을 ChatUser의 ChatRoom
     * @param user     찾을 ChatUser의 User
     * @return 찾은 ChatUser
     */
    ChatUser findChatUserByChatRoomAndUser(ChatRoom chatRoom, User user);

    /**
     * 초대할 userId list를 user list로 변환
     *
     * @param memberIdList userId list
     * @return user list
     */
    List<User> dtoToUserList(List<ChatMemberIdDto> memberIdList);
}
