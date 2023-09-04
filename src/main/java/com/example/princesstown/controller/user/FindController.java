package com.example.princesstown.controller.user;

import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.service.findPassword.AuthenticationService;
import com.example.princesstown.service.message.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/code")
public class FindController {

    private final AuthenticationService authenticationService;

    private final MessageService messageService;

    // 휴대폰 인증 코드 발송
    @PostMapping("/send-phone-verification-code")
    public ResponseEntity<ApiResponseDto> sendVerificationCode(@RequestParam("phoneNumber") String phoneNumber) throws Exception {
        return messageService.sendVerificationCode(phoneNumber);
    }

    // 휴대폰 인증 검사
    @PostMapping("/verify-phone")
    public ResponseEntity<ApiResponseDto> verifyPhoneNumber(@RequestParam("phoneNumber") String phoneNumber, @RequestParam("inputCode") String inputCode) {
        return authenticationService.verifyPhoneNumber(phoneNumber, inputCode);
    }

    // 휴대폰 인증 후 임시 비밀번호 전송
    @PostMapping("/send-temp-password")
    public ResponseEntity<ApiResponseDto> sendTemporaryPasswordAfterVerification(@RequestParam("phoneNumber") String phoneNumber, @RequestParam("email") String email) {
        return authenticationService.sendTemporaryPasswordAfterVerification(phoneNumber, email);
    }

    // 임시 로그인
    @PostMapping("/login-with-temp-password")
    public ResponseEntity<ApiResponseDto> tempLogin(@RequestParam("username") String username, @RequestParam("tempPassword") String tempPassword) {
        return authenticationService.unifiedLogin(username, tempPassword);
    }
}

