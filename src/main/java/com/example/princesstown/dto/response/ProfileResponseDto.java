package com.example.princesstown.dto.response;

import com.example.princesstown.entity.Location;
import com.example.princesstown.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileResponseDto {

    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phoneNumber;
    private String profileImage;
    private Double latitude;
    private Double longitude;
    private Double radius;

    public ProfileResponseDto(User user, Location location) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.profileImage = user.getProfileImage();

        if (location != null) {
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();
            this.radius = location.getRadius();
        } else {
            this.latitude = null;
            this.longitude = null;
            this.radius = null;
        }
    }
}