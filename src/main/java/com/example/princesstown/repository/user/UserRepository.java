package com.example.princesstown.repository.user;

import com.example.princesstown.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findBynickname(String nickname);

    User findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.username = :username AND u.email = :email")
    Optional<User> emailCheck(@Param("username") String username, @Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.username = :username AND u.phoneNumber = :phoneNumber")
    Optional<User> phoneCheck(@Param("username") String username, @Param("phoneNumber") String phoneNumber);

    @Query("SELECT u.username FROM User u WHERE u.email = :email")
    List<String> findIdByEmail(@Param("email") String email);

    Optional<User> findByPhoneNumberAndEmail(String phoneNumber, String email);
}
