package com.example.princesstown.dto.comment;

import com.example.princesstown.entity.CommentLikes;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentLikesResponseDto {
    private Long id;
    private Long user_id;
    private Long post_id;
    private Long comment_id;
    private String username;
    private String nickname;
    private boolean likes;
    private Long likeCnt;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public CommentLikesResponseDto(CommentLikes commentLikes) {
        this.id = commentLikes.getId();
        this.user_id = commentLikes.getUser().getUserId();
        this.post_id = commentLikes.getComment().getPost().getId();
        this.comment_id = commentLikes.getComment().getId();
        this.username = commentLikes.getUser().getUsername();
        this.nickname = commentLikes.getComment().getUser().getNickname();
        this.likes = commentLikes.isLikes();
        this.likeCnt = commentLikes.getComment().getLikeCnt();
        this.createdAt = commentLikes.getCreatedAt();
        this.modifiedAt = commentLikes.getModifiedAt();
    }
}
