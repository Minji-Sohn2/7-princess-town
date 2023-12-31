package com.example.princesstown.controller.chat;

import com.example.princesstown.dto.chat.ChatMessageDto;
import com.example.princesstown.dto.chatRoom.*;
import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.security.user.UserDetailsImpl;
import com.example.princesstown.service.chat.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatRooms")
@Slf4j(topic = "ChatRoomController")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping
    public ResponseEntity<ChatRoomInfoResponseDto> createChatRoom(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CreateChatRoomRequestDto createChatRoomRequestDto
    ) {
        log.info("채팅방 생성 컨트롤러 -> " + userDetails.getUser().getUsername());
        ChatRoomInfoResponseDto result = chatRoomService.createChatRoom(userDetails.getUser(), createChatRoomRequestDto);
        return ResponseEntity.status(201).body(result);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<List<ChatMessageDto>> getChatRoomChatMessages(
            @PathVariable Long roomId,
            @RequestParam("page") int page,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        log.info("채팅방 채팅 기록 조회 컨트롤러 -> " + roomId);
        List<ChatMessageDto> chatMessageDtoList = chatRoomService.getChatRoomChatMessages(roomId, page, userDetails.getUser());

        return ResponseEntity.ok().body(chatMessageDtoList);
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<ChatRoomInfoResponseDto> updateChatRoomName(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody ChatRoomNameRequestDto requestDto
    ) {
        log.info("채팅방 이름 수정 컨트롤러 -> " + userDetails.getUser().getUsername());
        ChatRoomInfoResponseDto result = chatRoomService.updateChatRoomName(roomId, userDetails.getUser(), requestDto);
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<ApiResponseDto> deleteChatRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        log.info("채팅방 삭제 컨트롤러 -> " + userDetails.getUser().getUsername());
        chatRoomService.deleteChatRoom(roomId, userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "채팅방 삭제 완료"));
    }

    @GetMapping("/{roomId}/members")
    public ResponseEntity<MemberInfoListDto> getChatRoomMembers(
            @PathVariable Long roomId
    ) {
        log.info("채팅방 멤버 조회 컨트롤러");
        MemberInfoListDto result = chatRoomService.getChatRoomMembers(roomId);
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/{roomId}/members")
    public ResponseEntity<ApiResponseDto> leaveChatRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        log.info("채팅방 멤버 나가기 컨트롤러");
        chatRoomService.leaveChatRoom(roomId, userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "채팅방 나가기 완료"));
    }

    @PostMapping("/{roomId}/members")
    public ResponseEntity<ApiResponseDto> inviteMember(
            @PathVariable Long roomId,
            @RequestBody MemberIdListDto memberIdListDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        log.info("채팅방 멤버 초대 컨트롤러");
        chatRoomService.inviteMember(roomId, memberIdListDto, userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "채팅방에 초대 완료"));
    }

    @GetMapping("/myRooms")
    public ResponseEntity<MyChatRoomResponseDto> getMyChatRooms(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        log.info("내 채팅방 조회 컨트롤러 -> " + userDetails.getUser().getUsername());
        MyChatRoomResponseDto result = chatRoomService.getMyChatRooms(userDetails.getUser());
        return ResponseEntity.ok().body(result);
    }
}
