package com.example.princesstown.dto.comment;

import com.example.princesstown.entity.CommentLikes;
import com.example.princesstown.entity.ReplyLikes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReplyLikesResponseDto {
    private Long id;
    private Long user_id;
    private Long post_id;
    private Long comment_id;
    private String nickname;
    private boolean likes;
    private Long likeCnt;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public ReplyLikesResponseDto(ReplyLikes replyLikes) {
        this.id = replyLikes.getId();
        this.user_id = replyLikes.getReply().getUser().getId();
//        this.post_id = replyLikes.getReply().getPost().getId();
        this.comment_id = replyLikes.getReply().getId();
//        this.nickname = replyLikes.getReply().getUser().getNickname();
        this.likes = replyLikes.isLikes();
        this.likeCnt = replyLikes.getReply().getLikeCnt();
        this.createdAt = replyLikes.getCreatedAt();
        this.modifiedAt = replyLikes.getModifiedAt();
    }
}
