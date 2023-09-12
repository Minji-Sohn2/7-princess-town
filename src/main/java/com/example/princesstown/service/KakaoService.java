package com.example.princesstown.service;

import com.example.princesstown.dto.getInfo.KakaoUserInfoDto;
import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.entity.Location;
import com.example.princesstown.entity.User;
import com.example.princesstown.repository.kakao.KakaoRepository;
import com.example.princesstown.repository.user.UserRepository;
import com.example.princesstown.security.jwt.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    @Value("${kakao.client.id}")
    private String client_id;

    // 애플리케이션 등록 시 여러 개의 redirect_uri를 설정할 수 있으므로
    // 리다이렉트 경로를 명시해줘야 됨
    @Value("${kakao.redirect.url}")
    private String redirect_url;

    @Value("${kakao.client.secret}")
    private String client_secret;

    // 카카오 로그인 처리 메서드
    public ResponseEntity<ApiResponseDto> kakaoLogin(String code) throws IOException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        // 인가코드 : 인가 서버로부터 받는 액세스 토큰을 요청할 수 있는 코드
        // 액세스 토큰 : 인가 서버에서 가지고 있는 사용자 정보, 리소스 접근 권한을 가지고 있는 토큰
        String accessToken = getToken(code);
        log.info("인가 코드 : " + code);
        log.info("액세스 토큰 : " + accessToken);

        // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);

        // 3. 필요시에 회원가입
        User kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);

        // 4. 로그인
        ResponseEntity<ApiResponseDto> kakaoLoginResponse = kakaoLogin(kakaoUser);

        return kakaoLoginResponse;
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

        log.info("URI: {}", uri);

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
        log.info("Kakao accessToken : " + accessToken);

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
    private User registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {
        String kakaoUsername = kakaoUserInfo.getUsername() + "_KakaoUser_";
        User kakaoUser = kakaoRepository.findByUsernameStartingWith(kakaoUsername);
        log.info("user : " + kakaoUser);

        if (kakaoUser == null) {
            // nickname의 경우 중복 방지를 위해 무작위 UUID 추가 -> 프론트에서 프로필 재설정 필요 메세지 띄우기
            String uniqueNickname = kakaoUserInfo.getNickname() + "_Kakao";
            String uniqueUsername = kakaoUserInfo.getUsername() + "_KakaoUser_" + UUID.randomUUID();
            kakaoUserInfo.setNickname(uniqueNickname);
            kakaoUserInfo.setUsername(uniqueUsername);

            // password 생성
            String UUIDpassword = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(UUIDpassword);

            // 회원가입
            kakaoUser = new User(kakaoUserInfo, encodedPassword);
            log.info("kakao user : " + kakaoUser);

            // Location 정보 설정
            Location location = kakaoUser.getLocation();
            if (location == null) {
                // Location 정보가 없을 경우 기본 값 설정
                Location defaultLocation = new Location();
                defaultLocation.setLatitude(0.0);
                defaultLocation.setLongitude(0.0);
                kakaoUser.setLocation(defaultLocation);
            }

            // DB에 저장
            userRepository.save(kakaoUser);
        }
        return kakaoUser;
    }

    // 로그인
    public ResponseEntity<ApiResponseDto> kakaoLogin(User kakaoUser) {
        log.info("start");
        String kakaoUsername = kakaoUser.getUsername();
        String kakaoPassword = kakaoUser.getPassword();

        // 위에서 받아온 username과 일치하는 User 가져오기
        User checkKakaoUser = kakaoRepository.findByUsername(kakaoUsername);

        // DB에 없는 사용자인 경우 혹은 인코딩되지 않은 임시 비밀번호를 인코딩하여 DB 저장된 인코딩된 임시 비밀번호랑 같지 않을 때
        if (checkKakaoUser == null || !kakaoPassword.equals(checkKakaoUser.getPassword())) {
            log.info(checkKakaoUser.getPassword());
            log.info(kakaoUsername);
            log.info(kakaoPassword);
            log.error("로그인 정보가 일치하지 않습니다.");
            throw new IllegalArgumentException("로그인 정보가 일치하지 않습니다.");
        }

        // Location 정보 설정
        Location location = kakaoUser.getLocation();
        if (location == null) {
            // Location 정보가 없을 경우 기본 값 설정
            Location defaultLocation = new Location();
            defaultLocation.setLatitude(0.0);
            defaultLocation.setLongitude(0.0);
            kakaoUser.setLocation(defaultLocation);

            userRepository.save(kakaoUser);
        }

        // 토큰 생성
        String token = jwtUtil.createToken(kakaoUsername);
        log.info("token : " + token);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);

        return ResponseEntity.status(200).headers(headers).body(new ApiResponseDto(HttpStatus.OK.value(), " 카카오로 로그인이 성공적으로 되었습니다!.", checkKakaoUser));
    }
}
