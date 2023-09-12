package com.example.princesstown.controller.auth2;

import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.entity.User;
import com.example.princesstown.service.KakaoService;
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
public class KakaoController {

    private final KakaoService kakaoService;

    @GetMapping("/api/user/kakao/callback")
    public String kakaoLogin(@RequestParam String code) throws IOException {

        ResponseEntity<ApiResponseDto> responseDto = kakaoService.kakaoLogin(code);
        log.info("클라이언트에게 보낼 데이터 : " + responseDto);

        ApiResponseDto apiResponseBody = responseDto.getBody();
        User userData = (User) apiResponseBody.getData();
        String nickname = userData.getNickname();
        Long userId = userData.getUserId();
        String phoneNumber =userData.getPhoneNumber();
        String email = userData.getEmail();
        Double latitude = userData.getLocation().getLatitude();
        Double longitude = userData.getLocation().getLongitude();
        log.info("카카오서버에서 보내는 username : " + nickname);
        log.info("카카오서버에서 보내는 userId : " + userId);
        log.info("카카오서버에서 보내는 phoneNumber : " + phoneNumber);
        log.info("카카오서버에서 보내는 email : " + email);
        log.info("카카오서버에서 보내는 latitude : " + latitude);
        log.info("카카오서버에서 보내는 longitude : " + longitude);

        HttpHeaders apiRespnseHeader = responseDto.getHeaders();
        String token = apiRespnseHeader.getFirst("Authorization");

        if(token == null) {
            throw new RuntimeException("Token not found in headers");
        }
        log.info("카카오서버에서 보내는 token : " + token);

        String encodedNickname = URLEncoder.encode(nickname, "UTF-8");
        return "redirect:/view/mainpage?success=kakao&nickname=" + encodedNickname + "&userId=" + userId + "&token=" + token + "&phoneNumber=" + phoneNumber + "&email=" + email + "&latitude=" + latitude + "&longitude=" + longitude;
    }
}


