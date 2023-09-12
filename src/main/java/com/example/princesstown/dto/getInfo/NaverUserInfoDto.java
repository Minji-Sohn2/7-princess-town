package com.example.princesstown.dto.getInfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverUserInfoDto {
    private String username;
    private String nickname;

    public NaverUserInfoDto(String id, String nickname) {
        this.username = id;
        this.nickname = nickname;
    }
}
