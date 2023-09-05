package com.example.princesstown.repository.naver;

import com.example.princesstown.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NaverRepository extends JpaRepository<User, Long> {
    User findByUsernameStartingWith(String usernamePrefix);

    User findByUsername(String naverUsername);
}
