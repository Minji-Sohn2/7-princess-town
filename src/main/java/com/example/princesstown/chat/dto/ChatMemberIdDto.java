package com.example.princesstown.chat.dto;

import com.example.princesstown.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMemberIdDto {
    private Long userId;

    public ChatMemberIdDto(Long userId) {
        this.userId = userId;
    }

    public ChatMemberIdDto(User user) {
        this.userId = user.getId();
    }
}
