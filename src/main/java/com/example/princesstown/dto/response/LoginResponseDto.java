package com.example.princesstown.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginResponseDto {
    private Long userId;
    private String nickname;
    private String profileImage;
}
