package com.example.princesstown.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SignupRequestDto {

    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phoneNumber;
    private String profile_image_url;

}
