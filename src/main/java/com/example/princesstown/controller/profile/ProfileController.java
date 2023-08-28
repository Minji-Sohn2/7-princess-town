package com.example.princesstown.controller.profile;

import com.example.princesstown.dto.request.ProfileEditRequestDto;
import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.dto.response.UserResponseDto;
import com.example.princesstown.security.user.UserDetailsImpl;
import com.example.princesstown.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/auth/")
public class ProfileController {

    private final UserService userService;

    // 프로필 조회 메서드
    @GetMapping("/{userId}")
    @ResponseBody
    public UserResponseDto lookupUser(@PathVariable Long userId) {
        return userService.lookupUser(userId);
    }


    // 프로필 수정 메서드
    @PutMapping("/{userId}")
    @ResponseBody
    public ResponseEntity<ApiResponseDto> updateUser(@PathVariable Long userId, @RequestBody ProfileEditRequestDto updateRequestDto) {
        return userService.updateUser(userId, updateRequestDto);
    }


    // view.html
    @GetMapping("/profile")
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