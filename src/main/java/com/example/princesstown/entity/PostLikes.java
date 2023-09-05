package com.example.princesstown.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "likes")
public class PostLikes extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 연관관계 설정
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_userId")
    private User user;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Column
    private boolean likes;

    public PostLikes(boolean likes, Post post, User user) {
        this.likes = likes;
        this.post = post;
        this.user = user;
    }
}
