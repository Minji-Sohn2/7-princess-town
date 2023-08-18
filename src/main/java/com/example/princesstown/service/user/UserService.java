package com.example.princesstown.service.user;

import com.example.princesstown.dto.request.LoginRequestDto;
import com.example.princesstown.dto.request.ProfileEditRequestDto;
import com.example.princesstown.dto.request.SignupRequestDto;
import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.dto.response.UserResponseDto;
import com.example.princesstown.entity.User;
import com.example.princesstown.repository.user.UserRepository;
import com.example.princesstown.security.jwt.JwtUtil;
import com.example.princesstown.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j(topic = "UserService")
@RequiredArgsConstructor
@Service

public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final S3Uploader s3Uploader;

    public ResponseEntity<ApiResponseDto> signup(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String notEncodingPassword = requestDto.getPassword();

        // user 테이블에 입력받은 id와 동일한 데이터가 있는지 확인
        Optional<User> checkUser = userRepository.findByUsername(username);

        // 중복 회원 검증
        if (checkUser.isPresent()) {
            log.error(username + "와 중복된 사용자가 존재합니다.");
            return ResponseEntity.badRequest().body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "중복된 사용자가 존재합니다."));
        }

        // email 중복 확인
        String email = requestDto.getEmail();
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            return ResponseEntity.badRequest().body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "중복된 Email 입니다."));
        }

        // 비밀번호가 null 이거나 비어있는지 확인
        if (notEncodingPassword == null || notEncodingPassword.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "비밀번호가 존재하지 않습니다."));
        }

        // 비밀번호 형식 검증
        if (!Pattern.matches("^[a-zA-Z0-9!@#$%^&*()_+{}:\"<>?,.\\\\/]{8,15}$", notEncodingPassword)) {
            return ResponseEntity.badRequest().body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "비밀번호는 최소 8자 이상, 15자 이하이며 알파벳 대소문자, 숫자로 구성되어야 합니다."));
        }

        // S3에 이미지 업로드
        try {
            if (requestDto.getProfileImage() != null) {
                // S3에 이미지를 업로드하고, 이미지 URL을 받아옴
                String imageUrl = s3Uploader.upload(requestDto.getProfileImage(), "profile-images");
                requestDto.setProfileImageUrl(imageUrl);
            }
        } catch (IOException e) {
            // 로깅 또는 적절한 에러 메시지를 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "이미지 업로드 중 오류가 발생했습니다."));
        }

        // 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(notEncodingPassword);

        // 회원 가입 진행
        User user = new User(requestDto, encodedPassword);
        userRepository.save(user);

        return ResponseEntity.status(201).body(new ApiResponseDto(HttpStatus.CREATED.value(), "회원가입 완료 되었습니다."));
    }

    public ResponseEntity<ApiResponseDto> deleteAccount(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("존재하지 않는 사용자입니다.");
        }
        userRepository.deleteById(userId);

        return ResponseEntity.status(200).body(new ApiResponseDto(HttpStatus.OK.value(), "회원 탈퇴 성공"));
    }


    // 로그인 메서드
    public void login(LoginRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();

        // 위에서 받아온 username과 일치하는 User 가져오기
        Optional<User> checkUser = userRepository.findByUsername(username);

        // DB에 없는 사용자인 경우 혹은 비밀번호가 일치하지 않을 경우
        if (checkUser.isEmpty() || !passwordEncoder.matches(password, checkUser.get().getPassword())) {

            log.info(username);
            log.info(password);
            log.error("로그인 정보가 일치하지 않습니다.");
            throw new IllegalArgumentException("로그인 정보가 일치하지 않습니다.");
        }
    }

    // 로그아웃 메서드
    public ApiResponseDto logout(String token, String username) {
        // 토큰에서 "Bearer " 접두사 제거
        token = jwtUtil.substringToken(token);

        // 토큰을 블랙리스트에 추가
        tokenBlacklistService.addToBlacklist(token);

        // 새로운 토큰 생성
        String newToken = jwtUtil.createToken(username);

        // 새로운 토큰과 함께 응답 반환
        return new ApiResponseDto(HttpStatus.OK.value(), "로그아웃 되었습니다.");
    }



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
        UserResponseDto userResponseDto = new UserResponseDto(user);

        return ResponseEntity.status(200).body(new ApiResponseDto(HttpStatus.OK.value(), "프로필 수정 성공", userResponseDto)).getBody();
    }

    public UserResponseDto lookupUser(Long userId) {
        User user = findUser(userId);
        return new UserResponseDto(user);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("선택한 게시물은 존재하지 않습니다."));
    }
}
