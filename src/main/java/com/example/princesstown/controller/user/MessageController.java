package com.example.princesstown.controller.user;

import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.service.message.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponseDto> sendVerificationCode(@RequestParam("phoneNumber") String phoneNumber, HttpServletRequest request) {
        return messageService.sendVerificationCode(phoneNumber, request);
    }

    //문자 인증번호 검증
    @PostMapping("/verify/code")
    public ResponseEntity<ApiResponseDto> verifyCode(
            HttpServletRequest request,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("inputCode") String inputCode) {

        HttpSession session = request.getSession();
        String storedCode = (String) session.getAttribute(phoneNumber);

        if (storedCode == null) {
            return ResponseEntity.status(400).body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "인증 번호가 만료되었거나 없습니다.", null));
        }

        if (inputCode.equals(storedCode)) {
            // 인증 성공한 경우, 세션에서 인증 번호 삭제
            session.removeAttribute(phoneNumber);
            return ResponseEntity.status(200).body(new ApiResponseDto(HttpStatus.OK.value(), "인증 성공", null));
        } else {
            return ResponseEntity.status(400).body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "인증 실패", null));
        }
    }
}
