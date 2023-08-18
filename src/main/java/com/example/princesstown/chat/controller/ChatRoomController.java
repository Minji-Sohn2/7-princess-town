package com.example.princesstown.chat.controller;

import com.example.princesstown.chat.dto.ChatRoomInfoResponseDto;
import com.example.princesstown.chat.dto.ChatRoomNameRequestDto;
import com.example.princesstown.chat.dto.MemberIdListDto;
import com.example.princesstown.chat.dto.MyChatRoomResponseDto;
import com.example.princesstown.chat.service.ChatRoomService;
import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.security.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
@Slf4j(topic = "ChatRoomController")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomInfoResponseDto> createChatRoom(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody MemberIdListDto memberIdListDto
    ) {
        log.info("채팅방 생성 컨트롤러 -> " + userDetails.getUser().getUsername());
        ChatRoomInfoResponseDto result = chatRoomService.createChatRoom(userDetails.getUser(), memberIdListDto);
        return ResponseEntity.status(201).body(result);
    }

    @PutMapping("/rooms/{roomId}")
    public ResponseEntity<ChatRoomInfoResponseDto> updateChatRoomName(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody ChatRoomNameRequestDto requestDto
    ) {
        log.info("채팅방 이름 수정 컨트롤러 -> " + userDetails.getUser().getUsername());
        ChatRoomInfoResponseDto result = chatRoomService.updateChatRoomName(roomId, userDetails.getUser(), requestDto);
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/rooms/{roomId}")
    public ResponseEntity<ApiResponseDto> deleteChatRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        log.info("채팅방 삭제 컨트롤러 -> " + userDetails.getUser().getUsername());
        chatRoomService.deleteChatRoom(roomId, userDetails.getUser());
        return null;
    }

    @GetMapping("/rooms/{roomId}/members")
    public ResponseEntity<MemberIdListDto> getChatRoomMembers(
            @PathVariable Long roomId
    ) {
        log.info("채팅방 멤버 조회 컨트롤러");
        MemberIdListDto result = chatRoomService.getChatRoomMembers(roomId);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/rooms/myRooms")
    public ResponseEntity<MyChatRoomResponseDto> getMyChatRooms(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        log.info("내 채팅방 조회 컨트롤러 -> " + userDetails.getUser().getUsername());
        MyChatRoomResponseDto result = chatRoomService.getMyChatRooms(userDetails.getUser());
        return ResponseEntity.ok().body(result);
    }
}
