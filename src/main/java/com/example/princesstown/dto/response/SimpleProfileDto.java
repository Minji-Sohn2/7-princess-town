package com.example.princesstown.dto.response;

import com.example.princesstown.entity.User;
import lombok.Getter;

@Getter
public class SimpleProfileDto {
    private String username;
    private String nickname;
    private String profileImage;
    public SimpleProfileDto(User user) {
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.profileImage = user.getProfileImage();
    }
}
