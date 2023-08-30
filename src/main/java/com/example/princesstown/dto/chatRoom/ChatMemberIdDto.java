package com.example.princesstown.dto.chatRoom;

import com.example.princesstown.entity.ChatUser;
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
