package com.example.princesstown.chat.controller;

import com.example.princesstown.chat.ApiResponseDto;
import com.example.princesstown.chat.dto.ChatRoomInfoResponseDto;
import com.example.princesstown.chat.dto.ChatRoomNameRequestDto;
import com.example.princesstown.chat.dto.MemberIdListDto;
import com.example.princesstown.chat.service.ChatRoomService;
import com.example.princesstown.security.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomInfoResponseDto> createChatRoom(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody MemberIdListDto memberIdListDto
            ) {
//        ChatRoomInfoResponseDto result = chatRoomService.createChatRoom(userDetails.getUser(),memberIdListDto);
//        return ResponseEntity.status(201).body(result);
        return null;
    }

    @PutMapping("/rooms/{roomId}")
    public ResponseEntity<ChatRoomInfoResponseDto> updateChatRoomName(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody ChatRoomNameRequestDto requestDto
            ) {
//        ChatRoomInfoResponseDto result = chatRoomService.updateChatRoomName(roomId, userDetails.getUser(), requestDto);
//        return ResponseEntity.ok().body(result);
        return null;
    }

    @DeleteMapping("/rooms/{roomId}")
    public ResponseEntity<ApiResponseDto> deleteChatRoom (
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return null;
    }
}
