package com.example.princesstown.controller.profile;

import com.example.princesstown.dto.request.ProfileEditRequestDto;
import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.dto.response.ProfileResponseDto;
import com.example.princesstown.security.user.UserDetailsImpl;
import com.example.princesstown.service.profile.ProfileService;
import com.example.princesstown.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/users/profile")
public class ProfileController {

    private final UserService userService;
    private final ProfileService profileService;

    // 프로필 조회 메서드
    @GetMapping
    @ResponseBody
    public ProfileResponseDto lookupUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getUserId();
        ProfileResponseDto userResponse = userService.lookupUser(userId);

        log.info("프로필 조회 userId : " + userDetails.getUser().getUserId());
        log.info("프로필 조회 email : " + userResponse.getEmail());
        log.info("프로필 조회 phoneNumber : " + userResponse.getPhoneNumber());

        return userResponse;
    }

    // 프로필 수정
    @PutMapping
    @ResponseBody
    public ResponseEntity<ApiResponseDto> updateUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails, @ModelAttribute ProfileEditRequestDto editRequestDto) {
        Long userId = userDetails.getUser().getUserId();

        return profileService.updateUser(userId, editRequestDto);
    }
}
