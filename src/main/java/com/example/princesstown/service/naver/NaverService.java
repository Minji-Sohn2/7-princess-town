package com.example.princesstown.service.naver;

import com.example.princesstown.dto.getInfo.NaverUserInfoDto;
import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.entity.Location;
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
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponseDto> naverLogin(String code) throws IOException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        // 인가코드 : 인가 서버로부터 받는 액세스 토큰을 요청할 수 있는 코드
        // 액세스 토큰 : 인가 서버에서 가지고 있는 사용자 정보, 리소스 접근 권한을 가지고 있는 토큰
        String accessToken = getToken(code);

        // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        NaverUserInfoDto naverUserInfoDto = getNaverUserInfo(accessToken);

        // 3. 필요시에 회원가입
        User naverUser = registerNaverUserIfNeeded(naverUserInfoDto);

        // 4. 로그인
        ResponseEntity<ApiResponseDto> naverLoginResponse = naverLogin(naverUser);

        return naverLoginResponse;
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

        log.info("네이버 사용자 정보: " +  id + ", " + nickname);

        return new NaverUserInfoDto(id, nickname);
    }

    // 필요시에 회원가입하는 메서드
    private User registerNaverUserIfNeeded(NaverUserInfoDto naverUserInfo) {
        String naverUsername = naverUserInfo.getUsername() + "_NaverUser_";
        User naverUser = naverRepository.findByUsernameStartingWith(naverUsername);
        log.info("user : " + naverUser);

            if (naverUser == null) {
            // nickname의 경우 중복 방지를 위해 무작위 UUID 추가 -> 프론트에서 프로필 재설정 필요 메세지 띄우기
            String uniqueNickname = naverUserInfo.getNickname() + "_Naver";
            String uniqueUsername = naverUserInfo.getUsername() + "_NaverUser_" + UUID.randomUUID();
            naverUserInfo.setNickname(uniqueNickname);
            naverUserInfo.setUsername(uniqueUsername);

            // password 생성
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);

            // 회원가입
            naverUser = new User(naverUserInfo, encodedPassword);

                // Location 정보 설정
                Location location = naverUser.getLocation();
                if (location == null) {
                    // Location 정보가 없을 경우 기본 값 설정
                    Location defaultLocation = new Location();
                    defaultLocation.setLatitude(0.0);
                    defaultLocation.setLongitude(0.0);
                    naverUser.setLocation(defaultLocation);
                }

            // DB에 저장
            userRepository.save(naverUser);
        }
        return naverUser;
    }

    // 로그인
    public ResponseEntity<ApiResponseDto> naverLogin(User naverUser) {
        log.info("start");
        String naverUsername = naverUser.getUsername();
        String naverPassword = naverUser.getPassword();

        // 위에서 받아온 username과 일치하는 User 가져오기
        User checknaverUser = naverRepository.findByUsername(naverUsername);

        // DB에 없는 사용자인 경우 혹은 인코딩되지 않은 임시 비밀번호를 인코딩하여 DB 저장된 인코딩된 임시 비밀번호랑 같지 않을 때
        if (checknaverUser == null || !naverPassword.equals(checknaverUser.getPassword())) {
            log.info(checknaverUser.getPassword());
            log.info(naverUsername);
            log.info(naverPassword);
            log.error("로그인 정보가 일치하지 않습니다.");
            throw new IllegalArgumentException("로그인 정보가 일치하지 않습니다.");
        }

        // Location 정보 설정
        Location location = naverUser.getLocation();
        if (location == null) {
            // Location 정보가 없을 경우 기본 값 설정
            Location defaultLocation = new Location();
            defaultLocation.setLatitude(0.0);
            defaultLocation.setLongitude(0.0);
            naverUser.setLocation(defaultLocation);

            userRepository.save(naverUser);
        }

        // 토큰 생성
        String token = jwtUtil.createToken(naverUsername);
        log.info("token : " + token);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);

        return ResponseEntity.status(200).headers(headers).body(new ApiResponseDto(HttpStatus.OK.value(), " 네이버로 로그인이 성공적으로 되었습니다!.", checknaverUser));
    }
}
