package com.example.princesstown.service.findPassword;

import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.service.email.MailService;
import com.example.princesstown.service.message.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Service
public class AuthenticationService {

    private final MailService mailService;

    private final MessageService messageService;

    private final StringRedisTemplate redisTemplate;


    public ResponseEntity<ApiResponseDto> verifyPhoneNumber(String phoneNumber, String inputCode) {
        ResponseEntity<ApiResponseDto> phoneVerificationResponse = messageService.verifyCode(phoneNumber, inputCode);

        if(phoneVerificationResponse.getStatusCode() == HttpStatus.OK) {
            // 휴대폰 인증 성공시 Redis에 인증 성공 여부 저장
            redisTemplate.opsForValue().set(phoneNumber + "_verified", "true", 5, TimeUnit.MINUTES);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto(200, "휴대폰 인증 성공"));
        } else {
            return phoneVerificationResponse; // 휴대폰 인증 실패 응답 반환
        }
    }

    public ResponseEntity<ApiResponseDto> sendTemporaryPasswordAfterVerification(String phoneNumber, String email) {
        String isVerified = redisTemplate.opsForValue().get(phoneNumber + "_verified");
        if ("true".equals(isVerified)) {
            // 난수 비밀번호 생성 및 이메일 전송 로직
            String tempPassword = UUID.randomUUID().toString().substring(0, 11);
            mailService.sendTemporaryPassword(email);

            // 생성된 난수 비밀번호와 만료 시간을 Redis에 저장
            redisTemplate.opsForValue().set(email, tempPassword, 5, TimeUnit.MINUTES);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto(200, "임시 비밀번호 전송 성공"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(400, "휴대폰 인증이 필요합니다."));
        }
    }

    public ResponseEntity<ApiResponseDto> loginWithTemporaryPassword(String email, String inputTempPassword) {
        String storedTempPassword = redisTemplate.opsForValue().get(email);

        if (storedTempPassword == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(400, "임시 비밀번호 정보가 없습니다."));
        }

        if (inputTempPassword.equals(storedTempPassword)) {
            redisTemplate.delete(email); // 임시 비밀번호 사용 후 삭제
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto(200, "로그인 성공"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(400, "로그인 실패"));
        }
    }
}