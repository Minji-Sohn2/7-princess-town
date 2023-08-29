package com.example.princesstown.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Table(name = "chatroom")
@NoArgsConstructor
public class ChatRoom implements Serializable {

    @Serial
    private static final long serialVersionUID = 6494678977089006639L;

    @Id
    @GeneratedValue
    private Long id;

    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(length = 36)
    private String chatRoomId;

    private String chatRoomName;

    private Long hostUserId;

    /* 생성자 */
    public ChatRoom(User user, String chatRoomName) {
        this.chatRoomId = UUID.randomUUID().toString();
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
