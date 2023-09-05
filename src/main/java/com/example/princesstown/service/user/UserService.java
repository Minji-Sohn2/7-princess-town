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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
    private final ApplicationContext applicationContext;
    private final StringRedisTemplate redisTemplate;
    private final MailService mailService;


    public ResponseEntity<ApiResponseDto> signup(SignupRequestDto requestDto) {

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
                }
//                    // 이미지가 없는 경우 기본 이미지 URL을 설정
//                    imageUrl = s3Uploader.uploadDefaultImage(applicationContext);

//                // 이미지 URL을 설정
//                user.setProfileImage(imageUrl);

            } catch (IOException e) {
                // 로깅 또는 적절한 에러 메시지를 반환
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "이미지 업로드 중 오류가 발생했습니다."));
            }

            // User 객체 저장
            userRepository.save(user);

            // 회원가입 완료 후 인증상태 삭제
            redisTemplate.delete("VerificationStatus_" + requestDto.getPhoneNumber());

            return ResponseEntity.status(201).body(new ApiResponseDto(HttpStatus.CREATED.value(), "회원가입 완료 되었습니다."));
    }

    // 회원탈퇴할 때 인증코드 이메일로 보내기
    public ResponseEntity<ApiResponseDto> sendDeteactiveCode(String phoneNumber, String email) {
        Optional<User> user = userRepository.findByPhoneNumberAndEmail(phoneNumber, email);
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(400, "사용자를 찾을 수 없습니다."));
        } else {
             String username = user.get().getUsername();

            // 난수 탈퇴 인증코드 생성 및 이메일 전송 로직
            String deteactiveCode = UUID.randomUUID().toString().substring(0, 8);
            mailService.sendDeactivateVerifyCode(email, deteactiveCode);

            // 생성된 난수 인증코드 만료 시간을 Redis에 저장
            redisTemplate.opsForValue().set(username + "_deteactiveCode", deteactiveCode, 60, TimeUnit.MINUTES);

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto(200, "회원탈퇴 인증코드 전송 성공"));
        }
    }

    // 회원 탈퇴
    public ResponseEntity<ApiResponseDto> deleteAccount(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("존재하지 않는 사용자입니다.");
        }
        userRepository.deleteById(userId);

        return ResponseEntity.status(200).body(new ApiResponseDto(HttpStatus.OK.value(), "회원 탈퇴 성공"));
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
