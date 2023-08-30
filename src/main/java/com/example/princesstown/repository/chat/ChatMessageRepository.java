package com.example.princesstown.repository.chat;

import com.example.princesstown.entity.chat.ChatMessage;
import com.example.princesstown.entity.chat.ChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findAllByChatRoom(Pageable pageable, ChatRoom chatRoom);
}
