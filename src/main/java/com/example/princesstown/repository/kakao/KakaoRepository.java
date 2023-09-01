package com.example.princesstown.repository.kakao;

import com.example.princesstown.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KakaoRepository extends JpaRepository<User,Long> {
    User findByUsernameStartingWith(String usernamePrefix);

}
