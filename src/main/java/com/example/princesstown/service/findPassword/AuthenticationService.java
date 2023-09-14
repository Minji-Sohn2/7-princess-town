package com.example.princesstown.service.findPassword;

import com.example.princesstown.dto.request.LoginRequestDto;
import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.dto.response.LoginResponseDto;
import com.example.princesstown.entity.User;
import com.example.princesstown.repository.user.NotOptinalUserRepository;
import com.example.princesstown.repository.user.UserRepository;
import com.example.princesstown.security.jwt.JwtUtil;
import com.example.princesstown.service.email.MailService;
import com.example.princesstown.service.message.MessageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Service
@Slf4j
public class AuthenticationService {

    private final MailService mailService;

    private final MessageService messageService;

    private final StringRedisTemplate redisTemplate;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    private final NotOptinalUserRepository notOptinalUserRepository;


    public ResponseEntity<ApiResponseDto> verifyUsername(String username) {
        User user = notOptinalUserRepository.findByUsername(username);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto(HttpStatus.NOT_FOUND.value(), "아이디를 찾을 수 없습니다."));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto(HttpStatus.OK.value(), "아이디가 인증되었습니다.", user));
        }
    }


    // 휴대폰 인증 검사
    public ResponseEntity<ApiResponseDto> verifyPhoneNumber(String phoneNumber, String inputCode) {
        ResponseEntity<ApiResponseDto> phoneVerificationResponse = messageService.verifyCode(phoneNumber, inputCode);

        if (phoneVerificationResponse.getStatusCode() == HttpStatus.OK) {
            // 휴대폰 인증 성공시 Redis에 인증 성공 여부 저장
            redisTemplate.opsForValue().set(phoneNumber + "_verified", "true", 60, TimeUnit.MINUTES);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto(200, "휴대폰 인증 성공"));
        } else {
            return phoneVerificationResponse; // 휴대폰 인증 실패 응답 반환
        }
    }

    // 기존 아이디 이메일로 보내기
    public ResponseEntity<ApiResponseDto> sendUsernameAfterVerification(String phoneNumber, String email) {
        String isVerified = redisTemplate.opsForValue().get(phoneNumber + "_verified");
        Optional<User> user = userRepository.findByPhoneNumberAndEmail(phoneNumber, email);
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(400, "사용자를 찾을 수 없습니다."));
        }
        String username = user.get().getUsername();

        if ("true".equals(isVerified)) {

            mailService.sendUsernames(email, username);

            // username을 Redis에 저장
            redisTemplate.opsForValue().set(username + "_ID", username, 60, TimeUnit.MINUTES);

            // Redis에 저장된 phoneNumber + "_verified" 키를 삭제
            redisTemplate.delete(phoneNumber + "_verified");

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto(200, "임시 비밀번호 전송 성공"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(400, "휴대폰 인증이 필요합니다."));
        }
    }


    // 임시 비밀번호 이메일로 보내기
    public ResponseEntity<ApiResponseDto> sendTemporaryPasswordAfterVerification(String phoneNumber, String email) {
        String isVerified = redisTemplate.opsForValue().get(phoneNumber + "_verified");
        Optional<User> user = userRepository.findByPhoneNumberAndEmail(phoneNumber, email);
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(400, "사용자를 찾을 수 없습니다."));
        }
        String username = user.get().getUsername();

        if ("true".equals(isVerified)) {
            // 난수 비밀번호 생성 및 이메일 전송 로직
            String tempPassword = UUID.randomUUID().toString().substring(0, 8);
            mailService.sendTemporaryPassword(email, tempPassword);

            // 생성된 난수 비밀번호와 만료 시간을 Redis에 저장
            redisTemplate.opsForValue().set(username + "_tempPassword", tempPassword, 60, TimeUnit.MINUTES);

            // username을 Redis에 저장
            redisTemplate.opsForValue().set(username + "_ID", username, 60, TimeUnit.MINUTES);

            // Redis에 저장된 phoneNumber + "_verified" 키를 삭제
            redisTemplate.delete(phoneNumber + "_verified");

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto(200, "임시 비밀번호 전송 성공"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(400, "휴대폰 인증이 필요합니다."));
        }
    }


    // 임시 로그인 하기
    @Transactional
    public ResponseEntity<ApiResponseDto> unifiedLogin(String username, String tempPassword) {
        // Redis에 저장된 tempPassword와 username가져오기
        String storedTempPassword = redisTemplate.opsForValue().get(username + "_tempPassword");
        String storedUsername = redisTemplate.opsForValue().get(username + "_ID");
        log.info("storedTempPassword : " + storedTempPassword);
        log.info("storedUsername : " + storedUsername);

        //  Redis에 저장된 임시 비밀번호, 아이디가 만료되지 않았을 때
        if (Objects.equals(tempPassword, storedTempPassword) && Objects.equals(username, storedUsername)){

            // 기존 사용자 정보 로드
            Optional<User> user = userRepository.findByUsername(storedUsername);
            if (user.isPresent()) {
                User tempLoginUser = user.get();
                log.info("user : " + tempLoginUser.getUsername());

                // 비밀번호 인코딩 후 업데이트
                tempLoginUser.setPassword(passwordEncoder.encode(storedTempPassword));

                // User 객체 저장
                userRepository.save(tempLoginUser);

                // 로그인 객체 생성
                LoginRequestDto requestDto = new LoginRequestDto(storedUsername, storedTempPassword);

                // 임시 비밀번호로 로그인
                ResponseEntity<ApiResponseDto> tempLoginResponse = tempLogin(requestDto);
                if (tempLoginResponse.getStatusCode() != HttpStatus.OK) {
                    // tempLogin에서 로그인 실패시 해당 응답 반환
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(400, "로그인 실패. 회원 정보가 일치하지 않습니다."));
                }

                // 사용 후 Redis에 저장된 임시 비밀번호, 아이디 삭제
                redisTemplate.delete(username + "_tempPassword");
                redisTemplate.delete(username + "_ID");

                // 토큰 생성
                String token = jwtUtil.createToken(storedUsername);
                log.info("token : " + token);

                //헤더에 토큰 추가
                HttpHeaders headers = new HttpHeaders();
                headers.add(JwtUtil.AUTHORIZATION_HEADER, token);
                log.info("header info : " + headers);

                LoginResponseDto loginResponseDto = new LoginResponseDto(tempLoginUser.getUserId(), tempLoginUser.getNickname(),tempLoginUser.getProfileImage());

                return ResponseEntity.status(HttpStatus.OK).headers(headers).body(new ApiResponseDto(200, "로그인 성공. 임시 비밀번호로 로그인하였습니다. 비밀번호를 즉시 변경해 주세요.", loginResponseDto));

            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(400, "로그인 실패"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto(400, "로그인 실패. 임시 비밀번호나 아이디가 유효하지 않습니다."));
        }
    }

    // 임시 로그인
    public ResponseEntity<ApiResponseDto> tempLogin(LoginRequestDto requestDto) {
        log.info("start");
        String username = requestDto.getUsername();
        log.info("username: " + username);
        String password = requestDto.getPassword();
        log.info("password: " + password);

        // 위에서 받아온 username과 일치하는 User 가져오기
        Optional<User> checkUser = userRepository.findByUsername(username);

        log.info("checkUser: " + checkUser);
        // DB에 없는 사용자인 경우 혹은 인코딩되지 않은 임시 비밀번호를 인코딩하여 DB 저장된 인코딩된 임시 비밀번호랑 같지 않을 때
        if (checkUser.isEmpty() || !passwordEncoder.matches(password, checkUser.get().getPassword())) {

            log.info(username);
            log.info(password);
            log.error("로그인 정보가 일치하지 않습니다.");
            throw new IllegalArgumentException("로그인 정보가 일치하지 않습니다.");
        }
        return ResponseEntity.status(200).body(new ApiResponseDto(HttpStatus.OK.value(), " 임시 비밀번호로 로그인하였습니다. 비밀번호를 즉시 변경해 주세요.")); // 응답 수정
    }
}