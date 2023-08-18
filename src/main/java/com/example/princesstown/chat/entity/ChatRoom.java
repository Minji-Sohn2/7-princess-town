package com.example.princesstown.chat.entity;

import com.example.princesstown.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Table(name = "chatroom")
@NoArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatRoomName;

    private Long hostUserId;

    /* 생성자 */
    public ChatRoom(User user) {
        this.chatRoomName = UUID.randomUUID().toString();
        this.hostUserId = user.getUserId();
    }

    /* 연관관계 */
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatUser> chatUserList = new ArrayList<>();

    /* 서비스 메서드 */
    public void updateChatRoomName(String newChatRoomName) {
        this.chatRoomName = newChatRoomName;
    }
}
