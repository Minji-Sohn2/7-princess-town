package com.example.princesstown.controller.user;

import com.example.princesstown.dto.request.LoginRequestDto;
import com.example.princesstown.dto.request.SignupRequestDto;
import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.security.user.UserDetailsImpl;
import com.example.princesstown.service.message.MessageService;
import com.example.princesstown.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;
    private final StringRedisTemplate redisTemplate;
    private final MessageService messageService;

    // 휴대폰 인증 코드 발송
    @PostMapping("/send-phone-verification-code")
    public ResponseEntity<ApiResponseDto> sendVerificationCode(@RequestParam("phoneNumber") String phoneNumber) throws Exception {
        return messageService.sendVerificationCode(phoneNumber);
    }

    // 휴대폰 인증 검사
    @PostMapping("/verify-phone-code")
    public ResponseEntity<ApiResponseDto> verifyPhoneCode(@RequestParam("phoneNumber") String phoneNumber, @RequestParam("inputCode") String inputCode) {
        ResponseEntity<ApiResponseDto> response = messageService.verifyCode(phoneNumber, inputCode);
        if (response.getStatusCode() != HttpStatus.OK) {
            redisTemplate.opsForValue().set("VerificationStatus_" + phoneNumber, "false",1, TimeUnit.HOURS);
        } else {
            redisTemplate.opsForValue().set("VerificationStatus_" + phoneNumber, "true",1, TimeUnit.HOURS);
        }
        return response;
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDto> signup(@ModelAttribute @Valid SignupRequestDto requestDto) {
        if ("true".equals(redisTemplate.opsForValue().get("VerificationStatus_" + requestDto.getPhoneNumber()))) {
            return userService.signup(requestDto);
        } else {
            redisTemplate.delete("VerificationStatus_" + requestDto.getPhoneNumber());
            return ResponseEntity.badRequest().body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "휴대폰 인증을 다시 하시고 회원가입을 진행해주세요."));
        }
    }

    // 회원탈퇴
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponseDto> deleteAccount(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getUserId();
        return userService.deleteAccount(userId);
    }

    // 로그인
    @PostMapping("/login")
    public void login(@RequestBody LoginRequestDto requestDto) {
        log.error("start");
        userService.login(requestDto);
        log.error("login메서드 호출완료");
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDto> logout(@RequestHeader("Authorization") String token) {
        // UserService의 로그아웃 로직 호출
        ApiResponseDto response = userService.logout(token);
        return ResponseEntity.ok(response);
    }

    // view.html 부분
    @GetMapping("/login-page")
    public String loginAndsignupPage() {
        return "loginAndSignup";
    } // loginAndsignup.html view
}


