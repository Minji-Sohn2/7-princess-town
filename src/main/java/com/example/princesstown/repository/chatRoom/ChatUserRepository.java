package com.example.princesstown.repository.chatRoom;

import com.example.princesstown.entity.ChatRoom;
import com.example.princesstown.entity.ChatUser;
import com.example.princesstown.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {

    List<ChatUser> findAllByChatRoom(ChatRoom chatRoom);

    List<ChatUser> findAllByUser(User user);

    ChatUser findByChatRoomAndUser(ChatRoom chatRoom, User user);
}
