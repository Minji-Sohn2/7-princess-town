package com.example.princesstown.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class SignupRequestDto {

    @Size(min = 4, max = 20)
    private String username;
    private String password;
    private String nickname;

    @Email(message = "이메일 형식에 맞지 않습니다.")
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String email;

    @NotBlank
    private String phoneNumber;

    private MultipartFile profileImage;
    private String phoneVerifyCode;
    private Double latitude;
    private Double longitude;
    private Double radius;
}
