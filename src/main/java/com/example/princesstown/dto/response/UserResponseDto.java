package com.example.princesstown.dto.response;


import com.example.princesstown.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UserResponseDto {

    private Long userId;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phoneNumber;
    private String profile_image_url;

    public UserResponseDto(User user) {
        this.username = user.getUsername();
        this.userId = user.getUserId();
        this.password = user.getPassword();
        this.username = user.getNickname();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.profile_image_url = user.getProfile_image_url();
    }
}