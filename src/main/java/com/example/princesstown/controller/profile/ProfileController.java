package com.example.princesstown.controller.profile;

import com.example.princesstown.dto.request.ProfileEditRequestDto;
import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.dto.response.UserResponseDto;
import com.example.princesstown.security.user.UserDetailsImpl;
import com.example.princesstown.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    // 프로필 조회 메서드
    @GetMapping("/api/auth/profile")
    @ResponseBody
    public UserResponseDto lookupUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getUserId();
        UserResponseDto userResponse = userService.lookupUser(userId);

        return userResponse;
    }



    @PutMapping("/api/auth/profile")
    @ResponseBody
    public ApiResponseDto updateUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestPart("editRequest") String editRequestJson,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {

        Long userId = userDetails.getUser().getUserId();

        // JSON 문자열을 ProfileEditRequestDto 객체로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        ProfileEditRequestDto editRequestDto = objectMapper.readValue(editRequestJson, ProfileEditRequestDto.class);

        // MultipartFile을 DTO에 설정
        editRequestDto.setProfileImage(profileImage);

        return userService.updateUser(userId, editRequestDto);
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
