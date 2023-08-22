package com.example.princesstown.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Table(name = "chatroom")
@NoArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(length = 36)
    private String id;

    private String chatRoomName;

    private int memberCount;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private User hostUser;

    /* 생성자 */
    public ChatRoom(User user) {
        this.chatRoomName = UUID.randomUUID().toString();
        this.hostUser = user;
    }

    /* 연관관계 */
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatUser> chatUserList = new ArrayList<>();

    public void addChatUser(ChatUser chatUser) {
        this.chatUserList.add(chatUser);
    }

    /* 서비스 메서드 */
    public void updateChatRoomName(String newChatRoomName) {
        this.chatRoomName = newChatRoomName;
    }
}
