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

    @Column
    private String message;

    @Column
    private String imgUrl;

    @Column
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Long chatRoomId;

    public ChatMessage(User user, ChatMessageDto chatMessageDto) {
        this.senderId = user.getUserId();
        this.senderNickname = user.getNickname();
        if(chatMessageDto.getMessage() != null) {
            this.message = chatMessageDto.getMessage();
        }
        if(chatMessageDto.getImgData() != null) {
            this.imgUrl = chatMessageDto.getImgData();
        }
        this.chatRoomId = chatMessageDto.getRoomId();
        this.createdAt = LocalDateTime.now();
    }
}
