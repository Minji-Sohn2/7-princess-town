package com.example.princesstown.dto.getInfo;

import com.example.princesstown.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverUserInfoDto {
    private String username;
    private String nickname;
    private String phoneNumber;


    public NaverUserInfoDto(String id, String nickname, String phoneNumber) {
        this.username = id;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
    }

}
