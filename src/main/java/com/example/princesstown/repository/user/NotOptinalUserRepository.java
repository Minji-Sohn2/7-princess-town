package com.example.princesstown.repository.user;

import com.example.princesstown.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotOptinalUserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
