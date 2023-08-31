package com.example.princesstown.controller.user;

import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.service.message.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    //문자 인증번호 발송
    @PostMapping("/send/code")
    public ResponseEntity<ApiResponseDto> sendVerificationCode(@RequestParam("phoneNumber") String phoneNumber) throws Exception {
        return messageService.sendVerificationCode(phoneNumber);
    }

    //문자 인증번호 검증
    @PostMapping("/verify-code")
    public ResponseEntity<ApiResponseDto> verifyCode(@RequestParam("phoneNumber") String phoneNumber, @RequestParam("inputCode") String inputCode) {
        return messageService.verifyCode(phoneNumber, inputCode);
    }
}
