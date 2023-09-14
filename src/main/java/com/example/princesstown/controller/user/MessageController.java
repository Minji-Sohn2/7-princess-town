package com.example.princesstown.controller.user;

import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.service.message.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sms")
public class MessageController {

    private final MessageService messageService;

    //문자 인증번호 발송
    @PostMapping("/codes")
    public ResponseEntity<ApiResponseDto> sendVerificationCode(@RequestParam("phonenumber") String phoneNumber) throws Exception {
        return messageService.sendVerificationCode(phoneNumber);
    }

    //문자 인증번호 검증
    @PostMapping("verify-codes")
    public ResponseEntity<ApiResponseDto> verifyCode(@RequestParam("phonenumber") String phoneNumber, @RequestParam("inputcode") String inputCode) {
        return messageService.verifyCode(phoneNumber, inputCode);
    }
}
