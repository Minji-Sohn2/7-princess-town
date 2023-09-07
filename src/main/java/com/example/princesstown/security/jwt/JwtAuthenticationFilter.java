package com.example.princesstown.security.jwt;

import com.example.princesstown.dto.request.LoginRequestDto;
import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.dto.response.LoginResponseDto;
import com.example.princesstown.security.user.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    //  사용자의 로그인 요청을 처리하고, 인증에 성공한 경우 JWT 토큰을 생성하여 응답 헤더에 추가하는 필터
    //  JwtUtil을 사용하여 JWT 토큰을 생성하고, "Bearer " 접두사를 제거하는 등의 작업하는 필터임.

    // 프론트가 있을 때는 필터로 로그인 구현
    // 백 기능만 구현할 때는 컨트롤러-서비스 로 로그인 구현하는 것이 좋음

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;

        // login" URL로 들어오는 인증 요청을 처리하도록 설정
        setFilterProcessesUrl("/api/users/login");
    }

    @Override
    // 로그인 요청 처리
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            log.info("Attempting authentication");
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);
            log.info("Parsed request body to LoginRequestDto: " + requestDto);

            Authentication authentication = getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getUsername(),
                            requestDto.getPassword()
                    )
            );
            log.info("Authentication success for user: " + requestDto.getUsername());
            return authentication;

        } catch (IOException e) {
            log.error("IOException occurred during authentication", e);
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error occurred during authentication", e);
            throw e;
        }
    }

    @Override
    // 로그인 성공 처리
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("Successful authentication");
        // username,userId 받아오기
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        Long userId = ((UserDetailsImpl) authResult.getPrincipal()).getUserId();
        String nickname = ((UserDetailsImpl) authResult.getPrincipal()).getNickname();
        log.info("Authenticated username: " + username);
        log.info("Authenticated userId : " + userId);

        LoginResponseDto responseDto = new LoginResponseDto(userId, nickname);

        // JWT 생성 후 Response 객체의 헤더에 추가함
        String token = jwtUtil.createToken(username);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

        // 로그인 성공시 상태코드,메세지 반환
        ApiResponseDto apiResponseDto = new ApiResponseDto(HttpStatus.OK.value(), "로그인 성공", responseDto);
        String json = new ObjectMapper().writeValueAsString(apiResponseDto);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

    @Override
    // 인증 실패 시 401
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        log.error("Unsuccessful authentication", failed);
        response.setStatus(401);
    }
}
