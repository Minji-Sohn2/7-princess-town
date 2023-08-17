package com.example.princesstown.service.user;

import com.example.princesstown.dto.request.LoginRequestDto;
import com.example.princesstown.dto.request.ProfileEditRequestDto;
import com.example.princesstown.dto.request.SignupRequestDto;
import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.dto.response.UserResponseDto;
import com.example.princesstown.entity.User;
import com.example.princesstown.jwt.JwtUtil;
import com.example.princesstown.repository.user.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j(topic = "UserService")
@RequiredArgsConstructor
@Service

public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public void signup(SignupRequestDto requestDto){
        // 1. 입력받은 id와 password 를 저장합니다.
        //    password 는 암호화가 이뤄집니다.
        String username = requestDto.getUsername();
        String notEncodingPassword = requestDto.getPassword();

        if (notEncodingPassword == null || notEncodingPassword.isEmpty()) {
            log.error("Password cannot be null or empty");
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
            String password = passwordEncoder.encode(notEncodingPassword);
            log.info("password 인코딩 후 받아오기 성공");

        // 2. user 테이블에 입력받은 id와 동일한 데이터가 있는지 확인합니다.
        Optional<User> checkUser = userRepository.findByUsername(username);

        // 2-1. 중복 회원이 있을 경우
        if (checkUser.isPresent()) {
            // 서버 측에 로그를 찍는 역할을 합니다.
            log.error(username + "와 중복된 사용자가 존재합니다.");
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        // 3. email 중복 확인
        String email = requestDto.getEmail();
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            log.error(checkEmail + "와 중복된 Email 이 존재합니다.");
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }

        // 4. 회원 가입 진행
        User user = new User(requestDto, password);
        userRepository.save(user);

        log.info(username + "님이 회원 가입에 성공하였습니다");
    }


    // 로그인 메서드
    public void login(LoginRequestDto requestDto, HttpServletResponse response) {
        // 클라이언트로 부터 전달 받은 id 와 password 를 가져옵니다.
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();

        // 사용자를 확인하고, 비밀번호를 확인합니다.
        Optional<User> checkUser = userRepository.findByUsername(username);

        // DB에 없는 사용자인 경우 혹은 비밀번호가 일치하지 않을 경우
        if (!checkUser.isPresent() || !passwordEncoder.matches(password, checkUser.get().getPassword())) {
            // 서버 측에 로그를 찍는 역할을 합니다.
            log.info(username);
            log.info(password);
            log.error("로그인 정보가 일치하지 않습니다.");
            throw new IllegalArgumentException("로그인 정보가 일치하지 않습니다.");
        }

        // JWT 생성 및 쿠키에 저장 후 Response 객체에 추가함
        String token = jwtUtil.createToken(requestDto.getUsername());
        log.info("token : " + token);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

        // 서버 측에 로그를 찍는 역할을 합니다.
        log.info(username + "님이 로그인에 성공하였습니다");
    }


    // 프로필 수정
    @Transactional
    public ResponseEntity<ApiResponseDto> updateUser(Long userId, ProfileEditRequestDto requestDto) {
        // DB 에서 해당 유저 가져오기
        Optional<User> updateUser = userRepository.findById(userId);

        // 비밀번호와 확인 비밀번호 일치 여부 판단
        if (!requestDto.equals(requestDto.getPassword())) {
            log.error("비밀번호가 일치하지 않습니다");
            return ResponseEntity.status(400).body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "프로필 수정 실패"));
        }

        String password = passwordEncoder.encode(requestDto.getPassword());
        User user = updateUser.get();
        user.editProfile(requestDto, password);

        // User -> UserResponseDto
        UserResponseDto userResponseDto = new UserResponseDto(user);

        ApiResponseDto result = new ApiResponseDto(HttpStatus.OK.value(), "프로필 수정 성공", userResponseDto);

        return ResponseEntity.status(200).body(result);
    }




    public UserResponseDto lookupUser(Long userId) {
        User user = findUser(userId);
        return new UserResponseDto(user);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("선택한 게시물은 존재하지 않습니다."));
    }

}
