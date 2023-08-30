package com.example.princesstown.service.findPassword;

import com.example.princesstown.dto.request.LoginRequestDto;
import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.entity.User;
import com.example.princesstown.repository.user.UserRepository;
import com.example.princesstown.security.jwt.JwtUtil;
import com.example.princesstown.service.email.MailService;
import com.example.princesstown.service.message.MessageService;
import com.example.princesstown.service.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Service
public class AuthenticationService {

    private final MailService mailService;

    private final MessageService messageService;

    private final StringRedisTemplate redisTemplate;

    private final UserService userService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;


    public ResponseEntity<ApiResponseDto> verifyPhoneNumber(String phoneNumber, String inputCode) {
        ResponseEntity<ApiResponseDto> phoneVerificationResponse = messageService.verifyCode(phoneNumber, inputCode);

        if (phoneVerificationResponse.getStatusCode() == HttpStatus.OK) {
            // 휴대폰 인증 성공시 Redis에 인증 성공 여부 저장
            redisTemplate.opsForValue().set(phoneNumber + "_verified", "true", 5, TimeUnit.MINUTES);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto(200, "휴대폰 인증 성공"));
        } else {
            return phoneVerificationResponse; // 휴대폰 인증 실패 응답 반환
        }
    }

    public ResponseEntity<ApiResponseDto> sendTemporaryPasswordAfterVerification(String phoneNumber, String email) {
        String isVerified = redisTemplate.opsForValue().get(phoneNumber + "_verified");
        Optional<User> user = userRepository.findByPhoneNumberAndEmail(phoneNumber, email);
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(400, "사용자를 찾을 수 없습니다."));
        }
        String username = user.get().getUsername();

        if ("true".equals(isVerified)) {
            // 난수 비밀번호 생성 및 이메일 전송 로직
            String tempPassword = UUID.randomUUID().toString().substring(0, 8);
            mailService.sendTemporaryPassword(email, tempPassword);

            // 생성된 난수 비밀번호와 만료 시간을 Redis에 저장
            redisTemplate.opsForValue().set(username + "_tempPassword", tempPassword, 5, TimeUnit.MINUTES);

            // username을 Redis에 저장
            redisTemplate.opsForValue().set(username + "_ID", username, 5, TimeUnit.MINUTES);

            // Redis에 저장된 phoneNumber + "_verified" 키를 삭제
            redisTemplate.delete(phoneNumber + "_verified");

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto(200, "임시 비밀번호 전송 성공"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(400, "휴대폰 인증이 필요합니다."));
        }
    }

    @Transactional
    public ResponseEntity<ApiResponseDto> unifiedLogin(String username, String tempPassword) {
        // Redis에 저장된 tempPassword와 username가져오기
        String storedTempPassword = redisTemplate.opsForValue().get(username + "_tempPassword");
        String storedUsername = redisTemplate.opsForValue().get(username + "_ID");

        //  Redis에 저장된 임시 비밀번호, 아이디가 만료되지 않았을 때
        if (Objects.equals(tempPassword, storedTempPassword) && Objects.equals(username, storedUsername)){

            // 기존 사용자 정보 로드
            Optional<User> user = userRepository.findByUsername(storedUsername);
            if (user.isPresent()) {
                User tempLoginUser = user.get();

                // 비밀번호 인코딩 후 업데이트
                tempLoginUser.setPassword(passwordEncoder.encode(storedTempPassword));

                // User 객체 저장
                userRepository.save(tempLoginUser);

                // 로그인 객체 생성
                LoginRequestDto requestDto = new LoginRequestDto(storedUsername, storedTempPassword);

                // 임시 비밀번호로 로그인
                userService.login(requestDto);

                // 사용 후 Redis에 저장된 임시 비밀번호, 아이디 삭제
                redisTemplate.delete(username + "_tempPassword");
                redisTemplate.delete(username + "_ID");

                // 토큰 생성
                String token = jwtUtil.createToken(storedUsername);

                // 헤더에 토큰 추가
                HttpHeaders headers = new HttpHeaders();
                headers.add(JwtUtil.AUTHORIZATION_HEADER, token);

                return ResponseEntity.status(HttpStatus.OK).headers(headers).body(new ApiResponseDto(200, "로그인 성공. 임시 비밀번호로 로그인하였습니다. 비밀번호를 즉시 변경해 주세요."));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(400, "로그인 실패"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(400, "로그인 실패. 임시 비밀번호나 아이디가 유효하지 않습니다."));
        }
    }
}