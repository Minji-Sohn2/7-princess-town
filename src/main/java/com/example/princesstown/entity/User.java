package com.example.princesstown.entity;

import com.example.princesstown.dto.getInfo.KakaoUserInfoDto;
import com.example.princesstown.dto.getInfo.NaverUserInfoDto;
import com.example.princesstown.dto.request.ProfileEditRequestDto;
import com.example.princesstown.dto.request.SignupRequestDto;
import com.example.princesstown.entity.chat.ChatUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phoneNumber;

    @Column
    private String profileImage;

    /* 연관관계 */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatUser> chatUserList = new ArrayList<>();

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "location_locationId")
    private Location location;

    public User(SignupRequestDto signupRequestDto, String encodedPassword) {
        this.username = signupRequestDto.getUsername();
        this.password = encodedPassword;
        this.nickname = signupRequestDto.getNickname();
        this.email = signupRequestDto.getEmail();
        this.phoneNumber = signupRequestDto.getPhoneNumber();
    }

    public User(KakaoUserInfoDto kakaoUserInfoDto, String encodedPassword) {
        this.nickname = kakaoUserInfoDto.getNickname();
        this.password = encodedPassword;
        this.username = kakaoUserInfoDto.getUsername();
    }

    public User(NaverUserInfoDto naverUserInfoDto, String encodedPassword) {
        this.password = encodedPassword;
        this.nickname = naverUserInfoDto.getNickname();
        this.username = naverUserInfoDto.getUsername();
        this.phoneNumber = naverUserInfoDto.getPhoneNumber();
    }

    public User(String storedUsername, String encodedTempPassword) {
        this.username = storedUsername;
        this.password = encodedTempPassword;
    }

    public void editProfile(ProfileEditRequestDto profileEditRequestDto, String password) {
        this.username = profileEditRequestDto.getUsername();
        this.password = password;
        this.nickname = profileEditRequestDto.getNickname();
        this.email = profileEditRequestDto.getEmail();
        this.phoneNumber = profileEditRequestDto.getPhoneNumber();
    }

//    public Optional<User> kakaoIdUpdate(String username) {
//        this.username = username;
//        return Optional.of(this);
//    }
}
