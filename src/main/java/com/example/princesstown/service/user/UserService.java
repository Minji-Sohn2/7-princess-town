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
import com.example.princesstown.service.message.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final MessageService messageService;
    private final ApplicationContext applicationContext;


    public ResponseEntity<ApiResponseDto> signup(SignupRequestDto requestDto) {
        // 휴대폰 인증 검사
        ResponseEntity<ApiResponseDto> phoneVerificationResponse = messageService.verifyCode(requestDto.getPhoneNumber(), requestDto.getPhoneVerifyCode());

        if (phoneVerificationResponse.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "휴대폰 인증 실패"));
        }

        String username = requestDto.getUsername();
        String notEncodingPassword = requestDto.getPassword();

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

        // 중복 전화번호 검증
        String phoneNumber = requestDto.getPhoneNumber();
        User checkPhoneNumber = userRepository.findByPhoneNumber(phoneNumber);
        if(checkPhoneNumber != null) {
            return ResponseEntity.badRequest().body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "중복된 전화번호 입니다."));
        }

            // 비밀번호 인코딩
            String encodedPassword = passwordEncoder.encode(notEncodingPassword);

            // 회원 가입 진행
            // requestDto.profileImage를 제외한 필드가 객체에 저장이 됨
            User user = new User(requestDto, encodedPassword);

            // S3에 이미지 업로드
            try {
                String imageUrl;

                if (requestDto.getProfileImage() != null) {
                    // S3에 이미지를 업로드하고, 이미지 URL을 받아와서 user.profileImage에 직접 객체에 저장이 됨
                    imageUrl = s3Uploader.upload(requestDto.getProfileImage(), "profile-images");
                } else {
                    // 이미지가 없는 경우 기본 이미지 URL을 설정
                    imageUrl = s3Uploader.uploadDefaultImage(applicationContext);
                }
                // 이미지 URL을 설정
                user.setProfileImage(imageUrl);

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

        // DB에 없는 사용자인 경우 혹은 인코딩되지 않은 임시 비밀번호를 인코딩하여 DB 저장된 인코딩된 임시 비밀번호랑 같지 않을 때
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

        /* redisTemplate.opsForValue().get(BLACKLIST_PREFIX + token)를 통해 토큰을 Redis에서 가져오는거 아닌이상,
             해당 토큰의 만료시간과 최대한 가깝게 Redis를 이용해 BlackList에 넣어둬야됨 */
        tokenBlacklistService.addToBlacklist(token);

        // 로그아웃 완료
        return new ApiResponseDto(HttpStatus.OK.value(), "로그아웃 되었습니다.");
    }

    // 유저조회
    public ProfileResponseDto lookupUser(Long userId) {
        return  userRepository.findById(userId)
                .map(user -> new ProfileResponseDto(user, user.getLocation())).orElseThrow(
                () -> new IllegalArgumentException("선택한 게시물은 존재하지 않습니다."));
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
