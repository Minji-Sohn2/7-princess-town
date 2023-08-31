package com.example.princesstown.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "commentlikes")
public class CommentLikes extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private boolean likes;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_userId")
    private User user;

    public CommentLikes(boolean likes, Comment comment, Post post, User user) {
        this.likes = likes;
        this.comment = comment;
        this.post = post;
        this.user = user;
    }
}
