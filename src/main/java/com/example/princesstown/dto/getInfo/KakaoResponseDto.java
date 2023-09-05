package com.example.princesstown.dto.getInfo;

import com.example.princesstown.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class KakaoResponseDto {
    private String jwtToken;
    private User kakaoUser;
}
