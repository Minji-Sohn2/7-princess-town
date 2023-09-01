package com.example.princesstown.service.naver;

import com.example.princesstown.dto.getInfo.KakaoResponseDto;
import com.example.princesstown.dto.getInfo.NaverResponseDto;
import com.example.princesstown.dto.getInfo.NaverUserInfoDto;
import com.example.princesstown.entity.User;
import com.example.princesstown.repository.naver.NaverRepository;
import com.example.princesstown.repository.user.UserRepository;
import com.example.princesstown.security.jwt.JwtUtil;
import com.example.princesstown.service.awsS3.S3Uploader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Slf4j(topic = "네이버 로그인")
@Service
@RequiredArgsConstructor
public class NaverService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final NaverRepository naverRepository;
    private final JwtUtil jwtUtil;
    private final S3Uploader s3Uploader;
    private final RestTemplate restTemplate;
    private final ApplicationContext applicationContext;

    @Value("${naver.client.id}")
    private String client_id;

    @Value("${naver.client.secret}")
    private String client_secret;

    // 애플리케이션 등록 시 하나의 redirect_uri를 설정할 수 있으므로
    // 리다이렉트 경로를 명시해주지 않아도 자동으로 가져 리다이렉트 됨
//   @Value("${naver.redirect.url}")
//   private String redirect_url;

    // 네이버 로그인 처리 메서드
    public NaverResponseDto naverLogin(String code) throws IOException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        // 인가코드 : 인가 서버로부터 받는 액세스 토큰을 요청할 수 있는 코드
        // 액세스 토큰 : 인가 서버에서 가지고 있는 사용자 정보, 리소스 접근 권한을 가지고 있는 토큰
        String accessToken = getToken(code);

        // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        NaverUserInfoDto naverUserInfoDto = getNaverUserInfo(accessToken);

        // 3. 필요시에 회원가입
        User naverUser = registerNaverUserIfNeeded(naverUserInfoDto);

        // 4. JWT 토큰 반환
        String createToken = jwtUtil.createToken(naverUser.getUsername()).substring(7);

        return new NaverResponseDto(createToken, naverUser);
    }

    // "인가 코드"로 "액세스 토큰" 요청하는 메서드
    private String getToken(String code) throws JsonProcessingException {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://nid.naver.com")
                .path("/oauth2.0/token")
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", client_id)
                .queryParam("client_secret", client_secret)
                .queryParam("code", code)
                .queryParam("state", "1234") // state: 임의 값 1234로 설정
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP GET 요청 보내기
        RequestEntity<Void> requestEntity = RequestEntity
                .get(uri)
                .headers(headers)
                .build();

        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }

    // 토큰으로 네이버 API 호출하여 "네이버 사용자 정보" 가져오는 메서드
    private NaverUserInfoDto getNaverUserInfo(String accessToken) throws JsonProcessingException {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/nid/me")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP GET 요청 보내기
        // get요청에서는 .build()를 사용해야됨 (본문이 필요x)
        RequestEntity<Void> requestEntity = RequestEntity
                .get(uri)
                .headers(headers)
                .build();

        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        // HTTP 응답 (JSON) -> 네이버 사용자 정보 파싱
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        String id = jsonNode.get("response").get("id").asText(); // 네이버 식별자 id
        String nickname = jsonNode.get("response").get("nickname").asText(); // 네이버 별명
        String phoneNumber = jsonNode.get("response").get("mobile").asText(); // 전화번호

        log.info("네이버 사용자 정보: " +  id + ", " + nickname + ", " + phoneNumber);

        return new NaverUserInfoDto(id, nickname, phoneNumber);
    }

    // 필요시에 회원가입하는 메서드
    private User registerNaverUserIfNeeded(NaverUserInfoDto naverUserInfo) throws IOException {
        String naverUsername = naverUserInfo.getUsername() + "_NaverUsername_";
        User naverUser = naverRepository.findByUsernameStartingWith(naverUsername);

            if (naverUser == null) {
            // nickname의 경우 중복 방지를 위해 무작위 UUID 추가 -> 프론트에서 프로필 재설정 필요 메세지 띄우기
            String uniqueNickname = naverUserInfo.getNickname() + "_NaverNickame_" + UUID.randomUUID();
            String uniqueUsername = naverUserInfo.getUsername() + "_NaverUsername_" + UUID.randomUUID();
            naverUserInfo.setNickname(uniqueNickname);
            naverUserInfo.setUsername(uniqueUsername);

            // password 생성
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);

            // 회원가입
            naverUser = new User(naverUserInfo, encodedPassword);

//            // 기본 이미지 설정
//            String imageUrl = s3Uploader.uploadDefaultImage(applicationContext);
//            naverUser.setProfileImage(imageUrl);

            // DB에 저장
            userRepository.save(naverUser);
        }
        return naverUser;
    }
}
