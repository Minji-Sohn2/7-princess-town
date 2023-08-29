package com.example.princesstown.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "chatroom")
@NoArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String chatRoomName;

    @Column(nullable = false)
    private Long hostUserId;

    /* 생성자 */
    public ChatRoom(User user, String chatRoomName) {
        this.chatRoomName = chatRoomName;
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
