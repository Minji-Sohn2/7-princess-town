package com.example.princesstown.dto.response;

import com.example.princesstown.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileResponseDto {

    private String username;
    private Long userId;
    private String password;
    private String nickname;
    private String email;
    private String phoneNumber;
    private String profileImage;

    public ProfileResponseDto(User user) {
        this.username = user.getUsername();
        this.userId = user.getUserId();
        this.password = "******************";
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.profileImage = user.getProfileImage();
    }
}
