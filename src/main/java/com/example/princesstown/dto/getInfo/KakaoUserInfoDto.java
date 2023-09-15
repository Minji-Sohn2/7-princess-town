package com.example.princesstown.dto.getInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoUserInfoDto {
    private String username;
    private String nickname;

    public KakaoUserInfoDto(String nickname, String id) {
        this.username = id;
        this.nickname = nickname;
    }

}
