package com.example.princesstown.controller.auth2;

import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.entity.User;
import com.example.princesstown.service.naver.NaverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.URLEncoder;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NaverController {

    private final NaverService naverService;

    @GetMapping("/api/user/naver/callback")
    public String NaverLogin(@RequestParam String code) throws IOException {

        ResponseEntity<ApiResponseDto> responseDto = naverService.naverLogin(code);
        log.info("클라이언트에게 보낼 데이터 : " + responseDto);

        ApiResponseDto apiResponseBody = responseDto.getBody();
        User userData = (User) apiResponseBody.getData();
        String nickname = userData.getNickname();
        Long userId = userData.getUserId();
        log.info("네이버서버에서 보내는 nickname : " + nickname);
        log.info("네이버서버에서 보내는 userId : " + userId);

        HttpHeaders apiRespnseHeader = responseDto.getHeaders();
        String token = apiRespnseHeader.getFirst("Authorization");

        if(token == null) {
            throw new RuntimeException("Token not found in headers");
        }
        log.info("네이버서버에서 보내는 token : " + token);

        String encodedNickname = URLEncoder.encode(nickname, "UTF-8");
        return "redirect:/view/login-page?success=naver&nickname=" + encodedNickname + "&userId=" + userId + "&token=" + token;
    }
}

