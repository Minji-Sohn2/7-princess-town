package com.example.princesstown.dto.response;

import com.example.princesstown.entity.PostLikes;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostLikesResponseDto {
    private Long id;
    private Long user_id;
    private Long post_id;
    private String username;
    private String nickname;
    private Boolean likes;
    private Long likeCnt;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public PostLikesResponseDto(PostLikes postLikes) {
        this.id = postLikes.getId();
        this.user_id = postLikes.getUser().getUserId();
        this.post_id = postLikes.getPost().getId();
        this.username = postLikes.getUser().getUsername();
        this.nickname = postLikes.getPost().getUser().getNickname();
        this.likes = postLikes.isLikes();
        this.likeCnt = postLikes.getPost().getLikeCnt();
        this.createdAt = postLikes.getCreatedAt();
        this.modifiedAt = postLikes.getModifiedAt();
    }
}
