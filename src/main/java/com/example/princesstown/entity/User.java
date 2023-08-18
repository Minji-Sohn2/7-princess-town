package com.example.princesstown.entity;

import com.example.princesstown.dto.request.ProfileEditRequestDto;
import com.example.princesstown.dto.request.SignupRequestDto;
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
    @Column(nullable = false, unique = true)
    private String email;
    @Column
    private String phoneNumber;
    @Column
    private String profile_image_url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    /* 연관관계 */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatUser> chatUserList = new ArrayList<>();

    public User(SignupRequestDto signupRequestDto, String password) {
        this.username = signupRequestDto.getUsername();
        this.password = password;
        this.nickname = signupRequestDto.getNickname();
        this.email = signupRequestDto.getEmail();
        this.phoneNumber = signupRequestDto.getPhoneNumber();
        this.profile_image_url = signupRequestDto.getProfile_image_url();
    }

    public void editProfile(ProfileEditRequestDto profileEditRequestDto, String password) {
        this.username = profileEditRequestDto.getUsername();
        this.password = password;
        this.nickname = profileEditRequestDto.getNickname();
        this.email = profileEditRequestDto.getEmail();
        this.phoneNumber = profileEditRequestDto.getPhoneNumber();
        this.profile_image_url = profileEditRequestDto.getProfile_image_url();
    }


}
