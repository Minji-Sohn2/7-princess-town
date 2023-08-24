package com.example.princesstown.config;

import com.example.princesstown.security.jwt.JwtAuthenticationFilter;
import com.example.princesstown.security.jwt.JwtAuthorizationFilter;
import com.example.princesstown.security.jwt.JwtUtil;
import com.example.princesstown.security.user.UserDetailsServiceImpl;
import com.example.princesstown.service.user.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
// 웹 애플리케이션의 보안을 설정하고 관리하고 JWT를 사용한 인증 및 권한 검사를 위한 필터를 설정하는 클래스
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final TokenBlacklistService tokenBlacklistService;

    // 비밀번호 암호화를 위한 Bean 설정
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // 인증 관리자 Bean 설정
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // JWT 인증 필터 Bean 설정
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    // JWT 권한 검사 필터 Bean 설정
    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService, tokenBlacklistService);
    }

    // Spring Security 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 보호 비활성화 : CSRF토큰은 공격을 방어하기 위한 토큰이고, 세션을 통해 담겨지므로 사용하는거지만,
        // JWT토큰을 사용할 것이기 때문에 CSRF 보호를 비활성화해야 함
        http.csrf(AbstractHttpConfigurer::disable);

        // 세션 생성 정책 설정: 세션을 생성하지 않음(CSRF를 사용하지 않기 때문)
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // HTTP 요청에 대한 접근 제어 설정
        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // 정적 리소스에 대한 접근 허용
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/view/**").permitAll()
                        .requestMatchers("/send/**").permitAll()
                        .requestMatchers("/modify/**").permitAll()
                        .requestMatchers("/find/**").permitAll()
                        .anyRequest().authenticated() // 그 외 요청은 인증 필요
        );


        // 로그인 페이지 설정
        http.formLogin((formLogin) ->
                formLogin
                        .loginPage("/auth/login-page").permitAll()
        );

        // JWT 관련 필터를 Spring Security 필터 체인에 추가
        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 예외 처리 설정: 접근 거부 페이지 설정
        http.exceptionHandling((exceptionHandling) ->
                exceptionHandling
                        .accessDeniedPage("/forbidden.html")
        );


        return http.build();
    }
}
