package com.example.princesstown.controller.chat;

import com.example.princesstown.dto.chat.ChatMessageDto;
import com.example.princesstown.security.user.UserDetailsImpl;
import com.example.princesstown.service.awsS3.S3Uploader;
import com.example.princesstown.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/*
    publisher 구현 (WebSocketChatHandler -> ChatController)
 */
@RequiredArgsConstructor
@Controller
@Slf4j(topic = "ChatController")
public class ChatController {

    private final ChatService chatService;
    private final S3Uploader s3Uploader;

    /*
        Websocket "/pub/chat/message"로 들어오는 메세지 처리
     */
    @MessageMapping("/chat/message")
    public void message(ChatMessageDto message, @Header("Authorization") String token) {
        log.info("ChatController - message 전송");

        // Websocket 에 발행된 메세지 redis 로 발행 (publish)
        chatService.sendChatMessage(message, token);
    }

    @PostMapping("/chat/file/{roomId}")
    public void messageFile(
            @PathVariable Long roomId,
            @RequestPart(value = "chatImage", required = false) MultipartFile chatImage,
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ) throws IOException {
        // s3 에 업로드
        String imageUrl = s3Uploader.upload(chatImage, "chat-images");

        chatService.sendImageMessage(roomId, imageUrl, userDetails.getUser());
    }
}
