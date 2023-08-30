package com.example.princesstown.repository.chat;

import com.example.princesstown.entity.User;
import com.example.princesstown.entity.chat.ChatRoom;
import com.example.princesstown.entity.chat.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {

    List<ChatUser> findAllByChatRoom(ChatRoom chatRoom);

    List<ChatUser> findAllByUser(User user);

    ChatUser findByChatRoomAndUser(ChatRoom chatRoom, User user);
}
