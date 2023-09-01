package com.example.princesstown.service;

import com.example.princesstown.dto.getInfo.KakaoResponseDto;
import com.example.princesstown.dto.getInfo.KakaoUserInfoDto;
import com.example.princesstown.entity.User;
import com.example.princesstown.repository.kakao.KakaoRepository;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Slf4j(topic = "KAKAO Login")
@Service
@RequiredArgsConstructor
public class KakaoService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final KakaoRepository kakaoRepository;
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;
    private final S3Uploader s3Uploader;
    private final ApplicationContext applicationContext;

    @Value("${kakao.client.id}")
    private String client_id;

    // 애플리케이션 등록 시 여러 개의 redirect_uri를 설정할 수 있으므로
    // 리다이렉트 경로를 명시해줘야 됨
    @Value("${kakao.redirect.url}")
    private String redirect_url;

    @Value("${kakao.client.secret}")
    private String client_secret;

    // 카카오 로그인 처리 메서드
    public KakaoResponseDto kakaoLogin(String code) throws IOException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        // 인가코드 : 인가 서버로부터 받는 액세스 토큰을 요청할 수 있는 코드
        // 액세스 토큰 : 인가 서버에서 가지고 있는 사용자 정보, 리소스 접근 권한을 가지고 있는 토큰
        String accessToken = getToken(code);

        // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);

        // 3. 필요시에 회원가입
        User kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);

        // 4. JWT 토큰 반환
        String createToken = jwtUtil.createToken(kakaoUser.getUsername()).substring(7);

        return new KakaoResponseDto(createToken, kakaoUser);
    }

    // "인가 코드"로 "액세스 토큰" 요청하는 메서드
    private String getToken(String code) throws JsonProcessingException {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com")
                .path("/oauth/token")
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", client_id)
                .queryParam("client_secret", client_secret)
                .queryParam("redirect_uri", redirect_url)
                .queryParam("code", code)
                .queryParam("state", "1234")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");


        // HTTP POST 요청 보내기
        // post요청에서 .body()를 사용하여 바디를 보내야됨(본문이 필요o)
        // 본문에 특별한 데이터를 보내지 않을 땐 NEW LinkedMultiValueMap<>()객체를 이용하여 빈 바디를 보내줌.
        // 본문에 "application/x-www-form-urlencoded;charset=utf-8" 이러한 데이터를 보낼때 MultiBalueMap을 사용함
        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());

        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }

    // 토큰으로 카카오 API 호출하여 "카카오 사용자 정보" 가져오는 메서드
    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com")
                .path("/v2/user/me")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP POST 요청 보내기
        // post요청에서 .body()를 사용하여 바디를 보내야됨(본문이 필요o)
        // 본문에 특별한 데이터를 보내지 않을 땐 NEW LinkedMultiValueMap<>()객체를 이용하여 빈 바디를 보내줌.
        // 본문에 "application/x-www-form-urlencoded;charset=utf-8" 이러한 데이터를 보낼때 MultiBalueMap을 사용함
        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());

        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        // HTTP 응답 (JSON) -> 카카오 사용자 정보 파싱
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        String id = jsonNode.get("id").asText(); // 카카오 식별 id
        String nickname = jsonNode.get("properties").get("nickname").asText(); // 카카오 아이디

        log.info("카카오 사용자 정보: " + id + ", " + nickname);
        return new KakaoUserInfoDto(nickname, id);
    }

    // 필요시에 회원가입하는 메서드
    private User registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) throws IOException {
        String kakaoUsername = kakaoUserInfo.getUsername() + "_kakaoUsername_";
        User kakaoUser = kakaoRepository.findByUsernameStartingWith(kakaoUsername);

        if (kakaoUser == null) {
            // nickname의 경우 중복 방지를 위해 무작위 UUID 추가 -> 프론트에서 프로필 재설정 필요 메세지 띄우기
            String uniqueNickname = kakaoUserInfo.getNickname() + "_KakaoNickname_" + UUID.randomUUID();
            String uniqueUsername = kakaoUserInfo.getUsername() + "_KakaoUsername_" + UUID.randomUUID();
            kakaoUserInfo.setNickname(uniqueNickname);
            kakaoUserInfo.setUsername(uniqueUsername);

            // password 생성
            String UUIDpassword = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(UUIDpassword);

            // 회원가입
            kakaoUser = new User(kakaoUserInfo, encodedPassword);

//            // 기본 이미지 설정
//            String imageUrl = s3Uploader.uploadDefaultImage(applicationContext);
//            kakaoUser.setProfileImage(imageUrl);

            // DB에 저장
            userRepository.save(kakaoUser);
        }
        return kakaoUser;
    }
}
