package com.example.princesstown.repository.chatRoom;

import com.example.princesstown.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
}
