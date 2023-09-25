package com.example.princesstown.dto.search;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@NoArgsConstructor
@Component
public class SimpleUserInfoDto {
    private Long userId;
    private String username;
    private String nickname;

    public SimpleUserInfoDto(Long id, String username, String nickname) {
        this.userId = id;
        this.username = username;
        this.nickname = nickname;
    }
}
