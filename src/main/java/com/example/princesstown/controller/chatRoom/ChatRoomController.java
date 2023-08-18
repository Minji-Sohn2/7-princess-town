package com.example.princesstown.controller.chatRoom;

import com.example.princesstown.dto.chatRoom.ChatRoomInfoResponseDto;
import com.example.princesstown.dto.chatRoom.ChatRoomNameRequestDto;
import com.example.princesstown.dto.chatRoom.MemberIdListDto;
import com.example.princesstown.dto.chatRoom.MyChatRoomResponseDto;
import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.security.user.UserDetailsImpl;
import com.example.princesstown.service.chatRoom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "채팅방 삭제 완료"));
    }

    @GetMapping("/rooms/{roomId}/members")
    public ResponseEntity<MemberIdListDto> getChatRoomMembers(
            @PathVariable Long roomId
    ) {
        log.info("채팅방 멤버 조회 컨트롤러");
        MemberIdListDto result = chatRoomService.getChatRoomMembers(roomId);
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/rooms/{roomId}/members")
    public ResponseEntity<ApiResponseDto> leaveChatRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        log.info("채팅방 멤버 나가기 컨트롤러");
        chatRoomService.leaveChatRoom(roomId, userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "채팅방 나가기 완료"));
    }

    @PostMapping("/rooms/{roomId}/members")
    public ResponseEntity<ApiResponseDto> inviteMember(
            @PathVariable Long roomId,
            @RequestBody MemberIdListDto memberIdListDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        log.info("채팅방 멤버 초대 컨트롤러");
        chatRoomService.inviteMember(roomId, memberIdListDto, userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "채팅방에 초대 완료"));
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
