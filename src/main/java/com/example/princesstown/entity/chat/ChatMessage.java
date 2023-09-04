package com.example.princesstown.entity.chat;

import com.example.princesstown.dto.chat.ChatMessageDto;
import com.example.princesstown.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatId;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false)
    private String senderNickname;

    @Column(nullable = false)
    private String message;

    @Column
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "chatRoom_id")
    private ChatRoom chatRoom;

    public ChatMessage(User user, ChatMessageDto chatMessage, ChatRoom chatRoom) {
        this.senderId = user.getUserId();
        this.senderNickname = user.getNickname();
        this.message = chatMessage.getMessage();
        this.chatRoom = chatRoom;
        this.createdAt = LocalDateTime.now();
    }
}
