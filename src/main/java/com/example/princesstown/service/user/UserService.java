package com.example.princesstown.service.user;

import com.example.princesstown.dto.request.LoginRequestDto;
import com.example.princesstown.dto.request.SignupRequestDto;
import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.dto.response.ProfileResponseDto;
import com.example.princesstown.dto.search.SearchUserResponseDto;
import com.example.princesstown.dto.search.SimpleUserInfoDto;
import com.example.princesstown.dto.search.UserSearchCond;
import com.example.princesstown.entity.User;
import com.example.princesstown.repository.user.UserRepository;
import com.example.princesstown.security.jwt.JwtUtil;
import com.example.princesstown.service.awsS3.S3Uploader;
import com.example.princesstown.service.email.MailService;
import com.example.princesstown.service.message.MessageService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j(topic = "UserService")
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final S3Uploader s3Uploader;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final MessageService messageService;
    public Map<String, Object> authNumMap = new HashMap<>();

    public ResponseEntity<ApiResponseDto> signup(SignupRequestDto requestDto, HttpServletRequest request) {
        String username = requestDto.getUsername();
        String notEncodingPassword = requestDto.getPassword();
//        String getPhoneVerifyCode = requestDto.getPhoneVerifyCode();

        // user 테이블에 입력받은 username과 동일한 데이터가 있는지 확인
        Optional<User> checkUser = userRepository.findByUsername(username);

        // 중복 회원 검증
        if (checkUser.isPresent()) {
            log.error(username + "와 중복된 사용자가 존재합니다.");
            return ResponseEntity.badRequest().body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "중복된 사용자가 존재합니다."));
        }

        // email 중복 확인
        String email = requestDto.getEmail();
        User checkEmail = userRepository.findByEmail(email);
        if (checkEmail != null) {
            return ResponseEntity.badRequest().body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "중복된 Email 입니다."));
        }

        // 비밀번호가 null 이거나 비어있는지 확인
        if (notEncodingPassword == null || notEncodingPassword.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "비밀번호가 존재하지 않습니다."));
        }

        // 비밀번호 형식 검증
        if (!Pattern.matches("^[a-zA-Z0-9!@#$%^&*()_+{}:\"<>?,.\\\\/]{8,15}$", notEncodingPassword)) {
            return ResponseEntity.badRequest().body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "비밀번호는 최소 8자 이상, 15자 이하이며 알파벳 대소문자, 숫자로 구성되어야 합니다."));
        }

//        // 휴대폰 인증 코드 검증
//        HttpSession session = request.getSession();
//        String storedCode = (String) session.getAttribute(requestDto.getPhoneNumber());
//
//        if (storedCode == null) {
//            return ResponseEntity.status(400).body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "인증 번호가 만료되었거나 없습니다."));
//        }
//
//        if (!getPhoneVerifyCode.equals(storedCode)) {
//            return ResponseEntity.badRequest().body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "휴대폰 인증 코드가 올바르지 않습니다."));
//        }
//
//        // 인증 성공한 경우, 세션에서 인증 번호 삭제
//        session.removeAttribute(requestDto.getPhoneNumber());


        // 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(notEncodingPassword);

        // 회원 가입 진행
        // requestDto.profileImage를 제외한 필드가 객체에 저장이 됨
        User user = new User(requestDto, encodedPassword);

        // S3에 이미지 업로드
        try {
            if (requestDto.getProfileImage() != null) {
                // S3에 이미지를 업로드하고, 이미지 URL을 받아와서 user.profileImage에 직접 객체에 저장이 됨
                String imageUrl = s3Uploader.upload(requestDto.getProfileImage(), "profile-images");
                user.setProfileImage(imageUrl);
            }
        } catch (IOException e) {
            // 로깅 또는 적절한 에러 메시지를 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "이미지 업로드 중 오류가 발생했습니다."));
        }
        // User 객체 저장
        userRepository.save(user);

        return ResponseEntity.status(201).body(new ApiResponseDto(HttpStatus.CREATED.value(), "회원가입 완료 되었습니다."));
    }


    // 회원 탈퇴
    public ResponseEntity<ApiResponseDto> deleteAccount(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("존재하지 않는 사용자입니다.");
        }
        userRepository.deleteById(userId);

        return ResponseEntity.status(200).body(new ApiResponseDto(HttpStatus.OK.value(), "회원 탈퇴 성공"));
    }


    // 로그인 메서드
    public void login(LoginRequestDto requestDto) {
        log.error("호출안됨");
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();

        // 위에서 받아온 username과 일치하는 User 가져오기
        Optional<User> checkUser = userRepository.findByUsername(username);

        // DB에 없는 사용자인 경우 혹은 비밀번호가 일치하지 않을 경우
        if (checkUser.isEmpty() || !passwordEncoder.matches(password, checkUser.get().getPassword())) {

            log.info(username);
            log.info(password);
            log.error("로그인 정보가 일치하지 않습니다.");
            throw new IllegalArgumentException("로그인 정보가 일치하지 않습니다.");
        }
    }

    // 로그아웃 메서드
    public ApiResponseDto logout(String token) {
        // 토큰에서 "Bearer " 접두사 제거
        token = jwtUtil.substringToken(token);

        // 접두사를 제거한 토큰을 블랙리스트에 추가
        tokenBlacklistService.addToBlacklist(token);

        // 새로운 토큰과 함께 응답 반환
        return new ApiResponseDto(HttpStatus.OK.value(), "로그아웃 되었습니다.");
    }

    // 유저조회
    public ProfileResponseDto lookupUser(Long userId) {
        return userRepository.findById(userId).map(ProfileResponseDto::new).orElseThrow(
                () -> new IllegalArgumentException("선택한 게시물은 존재하지 않습니다."));
    }

    // email로 id찾기
    public List<String> findId(String email) {
        return userRepository.findIdByEmail(email);
    }

    // email 확인
    public boolean emailCheck(String username, String email) {
        Optional<User> user = userRepository.emailCheck(username, email);
        return user.isPresent();
    }

    // 전화번호 확인
    public boolean phoneCheck(String username, String phoneNumber) {
        Optional<User> user = userRepository.phoneCheck(username, phoneNumber);
        return user.isPresent();
    }


    // 사용자 이름을 받아와서 JWT 토큰을 생성하고, 인증 상태를 관리하는 맵에 사용자 이름과 인증 상태를 저장
    public Map<String, Object> authenticateUser(String username) {
        // JWT 토큰 생성
        String token = jwtUtil.createToken(username);

        Map<String, Object> beforeAuthStatus = new HashMap<>();
        beforeAuthStatus.put("username", username);
        beforeAuthStatus.put("status", false);
        beforeAuthStatus.put("token", token);

        return beforeAuthStatus;
    }

    // 전화번호 또는 이메일을 받아와서 인증 번호를 생성하고, 해당 인증 번호의 생성 시간과 만료 시간을 관리하는 맵에 저장
    public String sendAuthNum(String phone, String email) {
        StringBuilder authNum = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            authNum.append((int) (Math.random() * 10));
        }

        // 전화번호로 인증번호 보내기 추가
        if (phone != null || email != null) {
            log.info("Sending authNum email to : {}", email);
            mailService.sendAuthNum(email, authNum.toString());
        }

        long createTime = System.currentTimeMillis(); // 인증번호 생성시간
        long endTime = createTime + (900 * 1000);    // 인증번호 만료시간

        authNumMap.put("createTime", createTime);
        authNumMap.put("endTime", endTime);
        authNumMap.put("authNum", authNum.toString());

        // 사용자 정보 조회
        Optional<User> user = userRepository.findByPhoneNumberAndEmail(phone, email);
        if (!user.isPresent()) {
            log.error("No user found for phone: " + phone + " and email: " + email);
            throw new IllegalArgumentException("User not found with provided phone and email");
        }
        String username = user.get().getUsername();
        log.info("Retrieved username from DB: " + username);
        authNumMap.put("username", username);

        log.info("Generated authNum: {}", authNum);

        // 인증정보을 포함한 authNum을 변환하여 JWT 토큰 생성
        return jwtUtil.createToken(authNumMap);
    }


    // sendAuthNum에서 authNum을 변환하여 전달한 JWT토큰을 헤더에서 JWT 토큰을 추출하고,
    // 해당 토큰의 유효성을 검사한 후 인증 상태를 업데이트하고 새로운 토큰을 생성
    public Map<String, Object> authCompletion(String tokenValue) {
        log.info("authCompletion started with tokenValue: " + tokenValue); // 시작 로그

        Claims claims = jwtUtil.getUserInfoFromToken(tokenValue);
        log.info("Extracted claims from token: " + claims); // 토큰에서 claims 추출 로그

        if (!isValidAuthenticationToken(tokenValue)) {
            log.error("Token is not valid for authentication");
            throw new IllegalArgumentException("유효하지 않은 인증번호입니다.");
        }

        Map<String, Object> afterAuthStatus = claims.get("authStatus", Map.class);
        if (afterAuthStatus == null) {
            log.error("authStatus from claims is null");
            throw new IllegalArgumentException("인증시간이 만료되었습니다");
        }
        log.info("Extracted authStatus from claims: " + afterAuthStatus); // authStatus 추출 로그

        afterAuthStatus.put("status", true);

        String username = (String) afterAuthStatus.get("username");

        if (username == null) {
            log.error("Username extracted from authStatus is null");
            throw new IllegalArgumentException("사용자 이름이 유효하지 않습니다.");
        }
        log.info("Extracted username from authStatus: " + username); // username 추출 로그

        String newToken = jwtUtil.createToken(afterAuthStatus);
        log.info("Created new token: " + newToken); // 새로운 토큰 생성 로그

        Map<String, Object> response = new HashMap<>();
        response.put("message", "인증이 완료되었습니다.");
        response.put("token", newToken);
        log.info("authCompletion ended with response: " + response); // 종료 로그

        return response;
    }

    // 헤더에서 JWT 토큰을 추출하고, 해당 토큰의 유효성을 검사한 후 사용자 이름을 반환
    public String auth(String tokenValue) {
        if (tokenValue == null || !tokenValue.startsWith(JwtUtil.BEARER_PREFIX)) {
            throw new IllegalArgumentException("인증 토큰이 없습니다.");
        }

        // JWT 토큰에서 사용자 이름 추출
        String token = tokenValue.substring(JwtUtil.BEARER_PREFIX.length());
        Claims claims = jwtUtil.getUserInfoFromToken(token);

        String usernameFromToken = claims.getSubject();
        if (usernameFromToken == null) {
            throw new IllegalArgumentException("인증 토큰이 유효하지 않습니다.");
        }

        return usernameFromToken;
    }

    // 현재 로그인된 사용자의 인증 토큰과 사용자 정보의 유효성을 검사
    public boolean validateModifyPasswordRequest(String authToken, String username) {
        log.info("authToken : " + authToken);

        if (authToken == null || authToken.isEmpty()) {
            return false;
        }

        // 접두어 "Bearer " 제거
        if (authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7);
        }

        String usernameFromToken = jwtUtil.getUserInfoFromToken(authToken).getSubject();
        log.info("Token에서 가져온 사용자 이름 : " + usernameFromToken);
        log.info("실제 이름 : " + username);

        return true;
    }

    //  사용자의 비밀번호를 변경하여 업데이트
    public void changePassword(String username, String newPassword) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("사용자가 존재하지 않습니다.");
        }

        User user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // 인증 번호 유효성 검증 로직
    private boolean isValidAuthenticationToken(String token) {
        Claims claims = jwtUtil.getUserInfoFromToken(token);

        Map<String, Object> authStatus = claims.get("authStatus", Map.class);
        log.info("authStatus 받아오기" + authStatus);
        if (authStatus == null) {
            // authStatus가 없으면 유효하지 않은 인증 토큰으로 처리
            return false;
        }
        return true;
    }

    // 사용자 검색
    @Transactional(readOnly = true)
    public SearchUserResponseDto searchUserByKeyword(UserSearchCond userSearchCond) {
        List<User> searchByUsername
                = userRepository.searchUserByKeyword(userSearchCond);


        List<User> searchByNickname
                = userRepository.searchNickByKeyword(userSearchCond);

        List<SimpleUserInfoDto> result = mergeUserResultLists(searchByUsername, searchByNickname)
                .stream()
                .map(SimpleUserInfoDto::new)
                .toList();

        return new SearchUserResponseDto(result);
    }

    // 검색 후 겹치는 아이디 제거
    private List<User> mergeUserResultLists(List<User> list1, List<User> list2) {
        Map<Long, User> map = new HashMap<>();

        for (User user1 : list1) {
            map.put(user1.getUserId(), user1);
        }

        for (User user2 : list2) {
            map.putIfAbsent(user2.getUserId(), user2);
        }

        return new ArrayList<>(map.values());
    }
}
