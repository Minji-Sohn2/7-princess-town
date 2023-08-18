package com.example.princesstown.chat.repository;

import com.example.princesstown.chat.entity.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {
    List<ChatUser> findAllByChatRoom_Id(Long chatRoomId);
}
