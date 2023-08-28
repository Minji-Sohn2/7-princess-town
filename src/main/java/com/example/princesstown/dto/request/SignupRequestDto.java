package com.example.princesstown.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class SignupRequestDto {
    private String username;
    private String password;
    private String nickname;

    @Email(message = "이메일 형식에 맞지 않습니다.")
    @NotBlank
    private String email;

    private String phoneNumber;
    private MultipartFile profileImage;
//    private String phoneVerifyCode;
}
