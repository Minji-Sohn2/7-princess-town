package com.example.princesstown.controller.profile;

import com.example.princesstown.dto.request.ProfileEditRequestDto;
import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.dto.response.ProfileResponseDto;
import com.example.princesstown.security.user.UserDetailsImpl;
import com.example.princesstown.service.location.LocationService;
import com.example.princesstown.service.profile.ProfileService;
import com.example.princesstown.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final LocationService locationService;
    private final ProfileService profileService;

    // 프로필 조회 메서드
    @GetMapping("/api/auth/profile")
    @ResponseBody
    public ProfileResponseDto lookupUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getUserId();
        ProfileResponseDto userResponse = userService.lookupUser(userId);

        return userResponse;
    }

    // 프로필 수정
    @PutMapping("/api/auth/profile")
    @ResponseBody
    public ApiResponseDto updateUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody ProfileEditRequestDto editRequestDto,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {

        Long userId = userDetails.getUser().getUserId();

        // 위치설정 업데이트 로직
//        if (editRequestDto.getLatitude() != null && editRequestDto.getLongitude() != null) {
//            locationService.updateLocationAndRelatedEntities(userId, editRequestDto.getLatitude(), editRequestDto.getLongitude());
//        }

        editRequestDto.setProfileImage(profileImage);

        return profileService.updateUser(userId, editRequestDto);
    }


    // view.html
    @GetMapping("/auth/profile")
    public String getPost(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        model.addAttribute("user", userDetails.getUser());

        return "profile";
    }

    @GetMapping("/update")
    public String updatePage(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        model.addAttribute("username",userDetails.getUser().getUsername());
        model.addAttribute("user",userDetails.getUser());

        return "profileUpdate";
    }
}