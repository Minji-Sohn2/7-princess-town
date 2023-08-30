package com.example.princesstown.controller.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@Slf4j(topic = "ChatRoomViewController")
@RequestMapping("/chat")
public class ChatRoomViewController {

    // 채팅 리스트 화면
    @GetMapping("/room")
    public String rooms() {
        log.info("rooms 보이기");
        return "chat/myChatRooms";
    }

    // 채팅방 입장 화면
    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId);
        return "chat/chatRoomDetail";
    }

}
