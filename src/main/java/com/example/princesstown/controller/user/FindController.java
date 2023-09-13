package com.example.princesstown.controller.user;

import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.entity.User;
import com.example.princesstown.service.findPassword.AuthenticationService;
import com.example.princesstown.service.message.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account-recovery")
public class FindController {
    private final AuthenticationService authenticationService;
    private final MessageService messageService;
    private final StringRedisTemplate redisTemplate;

    // 비밀번호 찾기 시 아이디 인증
    @PostMapping("/verify-usernames")
    public ResponseEntity<ApiResponseDto> verifyUsername(@RequestParam("username") String username) {
        ResponseEntity<ApiResponseDto> response = authenticationService.verifyUsername(username);
        ApiResponseDto apiResponseBody = response.getBody();
        User userData = (User) apiResponseBody.getData();
        String phoneNumber = userData.getPhoneNumber();
        log.info("userData : " + userData);
        log.info("phoneNumber : " + phoneNumber);

        try {
            if (response.getStatusCode() != HttpStatus.OK) {
                redisTemplate.opsForValue().set("VerificationStatus_ID" + phoneNumber, "false",1, TimeUnit.HOURS);
            } else {
                redisTemplate.opsForValue().set("VerificationStatus_ID" + phoneNumber, "true",1, TimeUnit.HOURS);
            }
        } catch(Exception e) {
            // Redis 작업 중 에러 처리
            log.info("에러메세지 : " + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto(HttpStatus.OK.value(), "아이디가 인증되었습니다."));
    }

    // 비밀번호 찾기 시 문자 인증번호 발송
    @PostMapping("/password/sms/codes")
    public ResponseEntity<ApiResponseDto> sendToMessageVerificationCode(@RequestParam("phoneNumber") String phoneNumber) throws Exception {
        if ("true".equals(redisTemplate.opsForValue().get("VerificationStatus_ID" + phoneNumber))) {
            redisTemplate.delete("VerificationStatus_ID" + phoneNumber);
            return messageService.sendVerificationCode(phoneNumber);
        } else {
            return ResponseEntity.badRequest().body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "휴대폰 인증을 다시 해주세요."));
        }
    }

    //아이디 찾기 시 문자 인증번호 발송
    @PostMapping("/usernames/sms/codes")
    public ResponseEntity<ApiResponseDto> sendToEmailVerificationCode(@RequestParam("phoneNumber") String phoneNumber) throws Exception {
        return messageService.sendVerificationCode(phoneNumber);
    }


    // 휴대폰 인증 검사
    @PostMapping("/sms/verify-codes")
    public ResponseEntity<ApiResponseDto> verifyPhoneNumber(@RequestParam("phoneNumber") String phoneNumber, @RequestParam("inputCode") String inputCode) {
        return authenticationService.verifyPhoneNumber(phoneNumber, inputCode);
    }

    // 휴대폰 인증 후 기존 아이디 전송
    @PostMapping("/usernames")
    public ResponseEntity<ApiResponseDto> findUsername(@RequestParam("phoneNumber") String phoneNumber, @RequestParam("email") String email) {
        return authenticationService.sendUsernameAfterVerification(phoneNumber, email);
    }

    // 휴대폰 인증 후 임시 비밀번호 전송
    @PostMapping("/temp-passwords")
    public ResponseEntity<ApiResponseDto> sendTemporaryPasswordAfterVerification(@RequestParam("phoneNumber") String phoneNumber, @RequestParam("email") String email) {
        return authenticationService.sendTemporaryPasswordAfterVerification(phoneNumber, email);
    }

    // 임시 로그인
    @PostMapping("/temp-login")
    public ResponseEntity<ApiResponseDto> tempLogin(@RequestParam("username") String username, @RequestParam("tempPassword") String tempPassword) {
        return authenticationService.unifiedLogin(username, tempPassword);
    }
}


