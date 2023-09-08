package com.example.princesstown.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProfileEditRequestDto {
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phoneNumber;
    private MultipartFile profileImage;
    private Double latitude;
    private Double longitude;
    private Double radius;
}
