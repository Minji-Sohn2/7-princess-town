package com.example.princesstown.controller.user;

import com.example.princesstown.security.jwt.JwtUtil;
import com.example.princesstown.security.user.UserDetailsImpl;
import com.example.princesstown.service.email.MailService;
import com.example.princesstown.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class FindController {

    private final UserService userService;
    private final MailService mailService;
    private final JwtUtil jwtUtil;

    // 메일로 아이디 보내기
    @PostMapping("/find/id/sendUsernames")
    public ResponseEntity<Object> sendEmail(@RequestParam String email) {
        List<String> usernames = userService.findId(email);

        if (usernames.size() != 0) {
            mailService.sendUsernames(email, usernames);
            return new ResponseEntity<>("Email이 성공적으로 보내졌습니다.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("해당 Email을 가진 유저를 찾지 못했습니다.", HttpStatus.NOT_FOUND);
        }
    }

    // username의 이메일이 맞는지 확인
    @GetMapping("/find/password/emailCheck")
    public ResponseEntity<Boolean> emailCheck(@RequestParam String username, @RequestParam String email){
        boolean emailCheck = userService.emailCheck(username, email);
        return new ResponseEntity<>(emailCheck, HttpStatus.OK);
    }

    // username의 전화번호가 맞는지 확인
    @GetMapping("/find/password/phoneCheck")
    public ResponseEntity<Boolean> phoneCheck(@RequestParam String username, @RequestParam String phoneNumber) {
        boolean phoneCheck = userService.phoneCheck(username, phoneNumber);
        return new ResponseEntity<>(phoneCheck, HttpStatus.OK);
    }

    // 사용자 이름을 받아와서 JWT 토큰을 생성하고, 인증 상태를 관리하는 맵에 사용자 이름과 인증 상태를 저장
    @PostMapping("/find/password/auth")
    public ResponseEntity<Object> authenticateUser(@RequestParam String username) {
        Map<String, Object> response = userService.authenticateUser(username);
        HttpHeaders headers = new HttpHeaders();
        headers.set(JwtUtil.AUTHORIZATION_HEADER, (String) response.get("token"));
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    // 전화번호 또는 이메일을 받아와서 인증 번호를 생성하고, 해당 인증 번호의 생성 시간과 만료 시간을 관리하는 맵에 저장
    @PostMapping("/send/authNum")
    public ResponseEntity<String> sendAuthNum(@RequestParam String phoneNumber, @RequestParam String email) {
        // authNum을 token으로 변환한것을 반환받음
        String transformationToken = userService.sendAuthNum(phoneNumber, email);
        HttpHeaders headers = new HttpHeaders();
        headers.set(JwtUtil.AUTHORIZATION_HEADER, transformationToken);
        return new ResponseEntity<>("인증번호가 전송되었습니다", headers, HttpStatus.OK);
    }

    // 헤더에서 JWT 토큰을 추출하고, 해당 토큰의 유효성을 검사한 후 인증 상태를 업데이트하고 새로운 토큰을 생성
    @PostMapping("/auth/completion")
    public ResponseEntity<Map<String, Object>> authCompletion(@RequestHeader(value = "Authorization") String transformationToken) {
        log.info("authCompletion method start");

        String tokenValue = jwtUtil.substringToken(transformationToken); // JWT 토큰 추출
        log.info("tokenValue extracted: " + tokenValue);

        Map<String, Object> response;
        try {
            response = userService.authCompletion(tokenValue);
        } catch (Exception e) {
            log.error("Error in authCompletion: " + e.getMessage()); // 예외 디버깅용 로그
            throw e; // 예외 다시 던지기 (또는 적절한 처리)
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(JwtUtil.AUTHORIZATION_HEADER, (String) response.get("token"));

        log.info("authCompletion method end"); // 디버깅용 로그
        return ResponseEntity.ok().headers(headers).body(response);
    }

    // JWT 토큰과 새로운 비밀번호를 받아와서, 해당 토큰의 유효성을 검증하고 사용자의 비밀번호를 변경
    @PutMapping("/modify/password")
    public ResponseEntity<String> modifyPassword(@RequestHeader(value = "Authorization") String authToken, @RequestParam String newPassword, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String username = userDetails.getUsername();
        log.info("비밀번호 변경을 위한 해당 유저이름 : " + username);
        log.info("authCompletion에서 받아온 새로운 비밀번호 : " + newPassword);
        log.info("authComplection에서 받아온 새로운 토큰값 : " + authToken);

        if (!userService.validateModifyPasswordRequest(authToken, username)) {
            log.error("유효성 검사 실패");
            return new ResponseEntity<>("인증이 실패하였습니다.", HttpStatus.UNAUTHORIZED);
        }

        userService.changePassword(username, newPassword);

        return new ResponseEntity<>("비밀번호를 변경하였습니다.", HttpStatus.OK);
    }

    // 비밀번호 찾기 페이지 반환
    @GetMapping("/find/password")
    public String findPassword() {
        return "/findPassword";
    }

    // 헤더에서 JWT 토큰을 추출하고, 해당 토큰의 유효성을 검사한 후 유효하면 인증 페이지를 반환
    @GetMapping("/find/password/auth")
    public String auth(HttpServletRequest request) {
        userService.auth(request.getHeader(JwtUtil.AUTHORIZATION_HEADER));
        return "/auth";
    }

    // 현재 로그인된 사용자의 인증 토큰과 사용자 정보의 유효성을 검사하고, 유효한 경우 비밀번호 변경 페이지를 반환
    @GetMapping("/modify/password")
    public String modifyPassword(@RequestHeader(value = "Authorization", required = false) String authToken, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String username = userDetails.getUsername();
        if (!userService.validateModifyPasswordRequest(authToken, username)) {
            return "redirect:/find/password";
        }
        return "/modifyPassword";
    }
}
