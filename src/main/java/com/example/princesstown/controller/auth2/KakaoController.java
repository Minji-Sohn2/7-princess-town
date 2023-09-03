package com.example.princesstown.controller.auth2;

import com.example.princesstown.dto.getInfo.KakaoResponseDto;
import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.security.jwt.JwtUtil;
import com.example.princesstown.service.KakaoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class KakaoController {

    private final KakaoService kakaoService;

    @GetMapping("/api/user/kakao/callback")
    public ResponseEntity<ApiResponseDto> kakaoLogin(
            @RequestParam String code, HttpServletResponse response) throws IOException {

        KakaoResponseDto responseDto = kakaoService.kakaoLogin(code);
        log.info("클라이언트에게 보낼 데이터 : " + responseDto);
        String pureToken = responseDto.getJwtToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set(JwtUtil.AUTHORIZATION_HEADER, pureToken);

        String authorizationHeader = headers.getFirst(JwtUtil.AUTHORIZATION_HEADER);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, authorizationHeader);

        return ResponseEntity.status(200).body(new ApiResponseDto(HttpStatus.OK.value(), "로그인 성공", responseDto));
    }
}
