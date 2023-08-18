package com.example.princesstown.chat.repository;

import com.example.princesstown.chat.entity.ChatRoom;
import com.example.princesstown.chat.entity.ChatUser;
import com.example.princesstown.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {
    List<ChatUser> findAllByChatRoom_Id(Long chatRoomId);

    List<ChatUser> findAllByUser(User user);

    ChatUser findByChatRoomAndUser(ChatRoom chatRoom, User user);
}
