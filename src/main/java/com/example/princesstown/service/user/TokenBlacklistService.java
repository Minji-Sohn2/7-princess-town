package com.example.princesstown.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final StringRedisTemplate stringRedisTemplate; // 레디스 템플릿

    private final String BLACKLIST_PREFIX = "BLACKLISTED_TOKEN ";

    // 블랙리스트에 토큰 추가
    public void addToBlacklist(String token) {
        stringRedisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "", 1, TimeUnit.HOURS); // 1시간 후 만료
    }

    // 블랙리스트에 있는 토큰인지 확인
    public boolean isBlacklisted(String token) {
        return stringRedisTemplate.hasKey(BLACKLIST_PREFIX + token);
    }
}