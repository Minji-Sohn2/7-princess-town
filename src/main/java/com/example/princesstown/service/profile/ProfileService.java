package com.example.princesstown.service.profile;

import com.example.princesstown.dto.request.ProfileEditRequestDto;
import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.dto.response.ProfileResponseDto;
import com.example.princesstown.entity.User;
import com.example.princesstown.repository.user.UserRepository;
import com.example.princesstown.service.awsS3.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")

public class ProfileService {
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;
    private final PasswordEncoder passwordEncoder;

    // 프로필 수정
    @Transactional
    public ApiResponseDto updateUser(Long userId, ProfileEditRequestDto requestDto) throws IOException {
        // DB 에서 해당 유저 가져오기
        Optional<User> updateUser = userRepository.findById(userId);


        // 유저 존재 유무 판단
        if (!updateUser.isPresent()) {
            log.error("해당 유저를 조회하지 못했습니다");
            return ResponseEntity.status(404).body(new ApiResponseDto(HttpStatus.NOT_FOUND.value(), "해당 유저를 조회하지 못했습니다")).getBody();
        }

        // 비밀번호 형식 검증
        if (!Pattern.matches("^[a-zA-Z0-9!@#$%^&*()_+{}:\"<>?,.\\\\/]{8,15}$", requestDto.getPassword())) {
            return new ApiResponseDto(400,"비밀번호는 최소 8자 이상, 15자 이하이며 알파벳 대소문자, 숫자로 구성되어야 합니다.", HttpStatus.BAD_REQUEST);
        }

        String profileImage = s3Uploader.upload(requestDto.getProfileImage(), "profile-images");
        String password = passwordEncoder.encode(requestDto.getPassword());
        User user = updateUser.get();
        user.editProfile(requestDto, password);
        user.setProfileImage(profileImage);

        // User -> UserResponseDto
        ProfileResponseDto profileResponseDto = new ProfileResponseDto(user);

        return ResponseEntity.status(200).body(new ApiResponseDto(HttpStatus.OK.value(), "프로필 수정 성공", profileResponseDto)).getBody();
    }
}
