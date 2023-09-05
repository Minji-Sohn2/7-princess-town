package com.example.princesstown.repository.chat;

import com.example.princesstown.entity.chat.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findAllByChatRoomIdAndCreatedAtGreaterThanEqual(Pageable pageable, Long chatRoomId, LocalDateTime enteredTime);
}
