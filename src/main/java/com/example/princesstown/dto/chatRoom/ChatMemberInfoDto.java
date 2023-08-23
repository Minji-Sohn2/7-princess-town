package com.example.princesstown.dto.chatRoom;

import com.example.princesstown.entity.ChatUser;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMemberInfoDto {
    private Long userId;
    private String username;
    private String nickname;

    public ChatMemberInfoDto(ChatUser chatUser) {
        this.userId = chatUser.getUser().getUserId();
        this.username = chatUser.getUser().getUsername();
        this.nickname = chatUser.getUser().getNickname();
    }
}
