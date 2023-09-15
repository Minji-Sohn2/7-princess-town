package com.example.princesstown.service.chat;

import com.example.princesstown.dto.chat.ChatMessageDto;
import com.example.princesstown.entity.User;

public interface ChatService {

    /**
     * "/sub/chat/{roomId}" -> {roomId}
     *
     * @param destination 원래 경로
     * @return 채팅방 id
     */
    String getRoomId(String destination);

    /**
     * 채팅 메세지 전송, 저장
     *
     * @param chatMessage 채팅 메세지
     * @param token       보낸 사람 정보가 담긴 token
     */
    void sendChatMessage(ChatMessageDto chatMessage, String token);

    /**
     * 파일 메세지 전송, 저장
     * @param roomId 채팅방 id
     * @param imageUrl s3 업로드 된 imageUrl
     * @param user 보낸 사람
     */
    void sendImageMessage(Long roomId, String imageUrl, User user);

    /**
     * username 으로 user 찾기
     *
     * @param username username
     * @return 해당하는 user
     */
    User findUserByUsername(String username);
}
