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
    private Long reply_id;
    private String username;
    private String nickname;
    private boolean likes;
    private Long likeCnt;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public ReplyLikesResponseDto(ReplyLikes replyLikes) {
        this.id = replyLikes.getId();
        this.user_id = replyLikes.getUser().getUserId();
        this.post_id = replyLikes.getReply().getPost().getId();
        this.comment_id = replyLikes.getComment().getId();
        this.reply_id = replyLikes.getReply().getId();
        this.username = replyLikes.getUser().getUsername();
        this.nickname = replyLikes.getReply().getUser().getNickname();
        this.likes = replyLikes.isLikes();
        this.likeCnt = replyLikes.getReply().getLikeCnt();
        this.createdAt = replyLikes.getCreatedAt();
        this.modifiedAt = replyLikes.getModifiedAt();
    }
}
