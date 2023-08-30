package com.example.princesstown.dto.search;

import com.example.princesstown.entity.User;
import lombok.Getter;

@Getter
public class SimpleUserInfoDto {
    private Long userId;
    private String username;
    private String nickname;

    public SimpleUserInfoDto(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
    }
}
