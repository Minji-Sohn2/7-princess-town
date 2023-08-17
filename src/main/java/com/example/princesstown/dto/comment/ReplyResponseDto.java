package com.example.princesstown.dto.comment;

import com.example.princesstown.entity.Reply;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReplyResponseDto {
    private Long id;
    private String content;
    private Long likeCnt;
    private Long comment_id;
    private Long user_id;
    private String username;
    private String nickname;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public ReplyResponseDto(Reply reply) {
        this.id = reply.getId();
        this.content = reply.getContent();
        this.likeCnt = reply.getLikeCnt();
        this.comment_id = reply.getComment().getId();
        this.user_id = reply.getUser().getId();
//        this.username = reply.getUser().getUsername();
//        this.nickname = reply.getUser().getNickname();
        this.createdAt = reply.getCreatedAt();
        this.modifiedAt = reply.getModifiedAt();
    }
}
