package com.example.princesstown.chat.entity;

import com.example.princesstown.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Table(name = "chatroom")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatRoomName;

    /* 생성자 */
    public ChatRoom () {
        this.chatRoomName = UUID.randomUUID().toString();
    }

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatUser> chatUserList = new ArrayList<>();

    /* 연관관계 편의 메서드 */
    public void addChatUser(List<User> userList) {
        for(User user : userList) {
            ChatUser chatUser = new ChatUser(user, this);
            this.chatUserList.add(chatUser);
            user.getChatUserList().add(chatUser);
        }
    }
}
