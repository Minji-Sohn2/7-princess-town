package com.example.princesstown.dto.getInfo;

import com.example.princesstown.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NaverResponseDto {
    private String jwtToken;
    private User naverUser;
}
