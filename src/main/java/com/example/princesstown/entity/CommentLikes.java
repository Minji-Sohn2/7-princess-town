package com.example.princesstown.entity;

import com.example.princesstown.dto.comment.CommentLikesRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "commentlikes")
public class CommentLikes extends TimeStamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private boolean likes;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @PostPersist
    @PostUpdate
    public void afterLikeChange() {
        if (likes) {
            comment.setLikeCnt(comment.getLikeCnt() + 1);
        } else {
            comment.setLikeCnt(comment.getLikeCnt() - 1);
        }
    }
}
