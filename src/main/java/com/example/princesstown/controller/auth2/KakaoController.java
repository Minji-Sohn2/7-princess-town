package com.example.princesstown.controller.auth2;

import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.security.jwt.JwtUtil;
import com.example.princesstown.service.KakaoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("kakao")
public class KakaoController {

    private final KakaoService kakaoService;

    @GetMapping("/api/user/kakao/callback")
    public ResponseEntity<ApiResponseDto> kakaoLogin(
            @RequestParam String code,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            HttpServletResponse response) throws IOException {

        String pureToken = kakaoService.kakaoLogin(code,profileImage).substring(7);
        HttpHeaders headers = new HttpHeaders();
        headers.set(JwtUtil.AUTHORIZATION_HEADER, pureToken);

        String authorizationHeader = headers.getFirst(JwtUtil.AUTHORIZATION_HEADER);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, authorizationHeader);

        return ResponseEntity.status(200).body(new ApiResponseDto(HttpStatus.OK.value(), "로그인 성공"));
    }
}
