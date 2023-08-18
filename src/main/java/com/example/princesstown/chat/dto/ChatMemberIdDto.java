package com.example.princesstown.chat.dto;

import com.example.princesstown.chat.entity.ChatUser;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMemberIdDto {
    private Long userId;

    public ChatMemberIdDto(ChatUser chatUser) {
        this.userId = chatUser.getUser().getUserId();
    }
}
