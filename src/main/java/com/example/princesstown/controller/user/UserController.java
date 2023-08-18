package com.example.princesstown.controller.user;

import com.example.princesstown.dto.request.LoginRequestDto;
import com.example.princesstown.dto.request.SignupRequestDto;
import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.service.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;


    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDto> signup( @RequestBody @Valid SignupRequestDto signupRequestDto, BindingResult bindingResult) {

        // 1. 예외 처리
        List<FieldError> fieldErrorList = bindingResult.getFieldErrors();
        if (fieldErrorList.size() > 0) {
            // DTO Validation 에 따른 예외 처리 발생 시 for 문을 통해 어떤 필드에 어떤 오류인지 message 를 출력합니다.
            // 해당 메시지는 userRequestDto 멩버변수에 Validation 어노테이션에 message 속성을 출력합니다.
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + "필드 : " + fieldError.getDefaultMessage());
            }
            // 회원가입 페이지 다시 로딩합니다.
//            return "redirect:/api/user/login-page";
        }
        // 2. 회원가입을 진행합니다.
        try {
            userService.signup(signupRequestDto); //회원 가입을 처리하기 위해 userService.signup(requestDto)를 호출
            log.info("signup 성공");
        } catch (IllegalArgumentException e) {
            log.error("signup 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "회원가입이 실패했습니다."));
        }
        return ResponseEntity.status(201).body(new ApiResponseDto(HttpStatus.CREATED.value(), "회원가입 완료 되었습니다."));
    }


    //로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto> login(@RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
        userService.login(requestDto, response);
        return ResponseEntity.status(201).body(new ApiResponseDto(HttpStatus.CREATED.value(), "로그인이 성공적으로 되었습니다."));
    }


//    // view.html 부분
//    @GetMapping("/login-page")
//    public String loginAndsignupPage() {
//        return "loginAndSignup";
//    } // loginAndsignup.html view
//

}


