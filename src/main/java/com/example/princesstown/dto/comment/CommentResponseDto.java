package com.example.princesstown.dto.comment;

import com.example.princesstown.entity.Comment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponseDto {
    private Long id;
    private String content;
    private String emoji;
    private Long likeCnt;
    private Long post_id;
    private Long user_id;
    private String username;
    private String nickname;
    private String img;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.emoji = comment.getEmoji();
        this.likeCnt = comment.getLikeCnt();
        this.post_id = comment.getPost().getId();
        this.user_id = comment.getUser().getUserId();
        this.username = comment.getUser().getUsername();
        this.nickname = comment.getUser().getNickname();
        this.img = comment.getUser().getProfile_image_url();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
    }
}
