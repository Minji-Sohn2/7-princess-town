package com.example.princesstown.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 연관관계 설정
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

//    @JsonIgnore
//    @ManyToOne
//    @JoinColumn(name = "comment_id")
//    private Comment comment;


    public Like(User user, Post post) {
        this.user = user;
        this.post = post;
    }

//    public Like(User user, Comment comment) {
//        this.user = user;
//        this.post = comment.getPost();
//        this.comment = comment;
//    }


}