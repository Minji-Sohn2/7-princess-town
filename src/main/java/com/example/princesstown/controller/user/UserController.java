package com.example.princesstown.controller.user;

import com.example.princesstown.dto.request.LoginRequestDto;
import com.example.princesstown.dto.request.SignupRequestDto;
import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.security.user.UserDetailsImpl;
import com.example.princesstown.service.findPassword.AuthenticationService;
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
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    private final MessageService messageService;

    private final StringRedisTemplate redisTemplate;
    private final AuthenticationService authenticationService;


    //문자 인증번호 발송
    @PostMapping("/sms/codes")
    public ResponseEntity<ApiResponseDto> sendVerificationCode(@RequestParam("phoneNumber") String phoneNumber) throws Exception {
        return messageService.sendVerificationCode(phoneNumber);
    }

    // 회원가입/탈퇴할 때 문자 인증 검사
    @PostMapping("/sms/verify-codes")
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
            redisTemplate.delete("VerificationStatus_" + requestDto.getPhoneNumber());
            return userService.signup(requestDto);
        } else {
            return ResponseEntity.badRequest().body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "휴대폰 인증을 다시 하시고 회원가입을 진행해주세요."));
        }
    }

    // 문자 인증 후 이메일로 회원탈퇴 인증코드 발송
    @PostMapping("/email/verify-codes")
    public ResponseEntity<ApiResponseDto> sendDeteactiveCode(@RequestParam("phoneNumber") String phoneNumber, @RequestParam("email") String email) {
        if ("true".equals(redisTemplate.opsForValue().get("VerificationStatus_" + phoneNumber)))  {
            redisTemplate.delete("VerificationStatus_" + phoneNumber);
            return userService.sendDeteactiveCode(phoneNumber, email);
        } else {
            return ResponseEntity.badRequest().body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "휴대폰 인증을 다시 하시고 회원탈퇴를 진행해주세요."));
        }
    }

    // 회원탈퇴
    @DeleteMapping("/account/deactivate")
    public ResponseEntity<ApiResponseDto> deleteAccount(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam("inputCode") String inputCode) {
        Long userId = userDetails.getUser().getUserId();
        String username = userDetails.getUser().getUsername();
        if(inputCode.equals(redisTemplate.opsForValue().get(username + "_deteactiveCode"))) {
            redisTemplate.delete(username + "_deteactiveCode");
            return userService.deleteAccount(userId);
        } else {
            return ResponseEntity.badRequest().body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "이메일 인증을 다시 하시고 회원탈퇴를 진행해주세요."));
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDto> logout(@RequestHeader("Authorization") String token) {
        // UserService의 로그아웃 로직 호출
        ApiResponseDto response = userService.logout(token);
        return ResponseEntity.ok(response);
    }
}


