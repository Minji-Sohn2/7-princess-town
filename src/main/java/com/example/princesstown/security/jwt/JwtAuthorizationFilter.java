package com.example.princesstown.security.jwt;

import com.example.princesstown.security.user.UserDetailsServiceImpl;
import com.example.princesstown.service.user.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {
/*  모든 요청에 대해 실행되며, 요청 헤더에서 JWT 토큰을 추출하고 해당 토큰의 유효성을 검증한 후,
    유효한 토큰인 경우 Spring Security의 SecurityContext에 인증 정보를 설정하는 필터  */
/*  JwtUtil을 사용하여 JWT 토큰의 유효성을 검증, 사용자 정보를 추출하고,
    UserDetailsServiceImpl을 사용하여 사용자의 상세 정보를 로드하는 역할을 함  */


    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    private final TokenBlacklistService tokenBlacklistService;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, TokenBlacklistService tokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        // 헤더에서 JWT 토큰 가져오기
        String tokenValue = req.getHeader(JwtUtil.AUTHORIZATION_HEADER);

        if (StringUtils.hasText(tokenValue)) {
            // JWT 토큰 substring
            tokenValue = jwtUtil.substringToken(tokenValue);
            log.info("Extracted token: " + tokenValue);

            // 유효성 검증
            if (!jwtUtil.validateToken(tokenValue) || tokenBlacklistService.isBlacklisted(tokenValue)) {
                log.error("Token Error: Invalid token or blacklisted");
                return;
            }

            Claims info = jwtUtil.getUserInfoFromToken(tokenValue);
            String username = info.getSubject();

                // 실제 사용자의 username을 가져오는 코드
                log.info("Setting authentication for username: " + username);
                setAuthentication(username);
        }

        filterChain.doFilter(req, res);
    }

    // 인증 처리
    public void setAuthentication(String username) {
        log.info("Setting authentication for username: " + username);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);

        log.info("Authentication set for username: " + username);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
