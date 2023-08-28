package com.example.princesstown.controller.auth2;

import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.security.jwt.JwtUtil;
import com.example.princesstown.service.naver.NaverService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class NaverController {

    private final NaverService naverService;

    @GetMapping("/api/user/kakao/callback")
    public ResponseEntity<ApiResponseDto> kakaoLogin(
            @RequestParam String code,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            HttpServletResponse response) throws IOException {

        String pureToken = naverService.naverLogin(code,profileImage).substring(7);
        HttpHeaders headers = new HttpHeaders();
        headers.set(JwtUtil.AUTHORIZATION_HEADER, pureToken);

        String authorizationHeader = headers.getFirst(JwtUtil.AUTHORIZATION_HEADER);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, authorizationHeader);

        return ResponseEntity.status(200).body(new ApiResponseDto(HttpStatus.OK.value(), "로그인 성공"));
    }
}

