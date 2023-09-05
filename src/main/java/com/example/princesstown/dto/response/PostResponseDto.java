package com.example.princesstown.dto.response;

import com.example.princesstown.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponseDto {
    private Long id; // 게시글 번호
    private String nickname; // 유저이름
    private String username;
    private String title; // 게시글 제목
    private String contents; // 게시글 내용
    private Long likeCnt; // 좋아요 수
    private int viewCount;
    private String postImageUrl;
    private LocalDateTime createdAt; // 게시글 생성시간
    private LocalDateTime modifiedAt; // 게시글 수정시간
//    private List<CommentResponseDto> commentList; // 게시글에 포함된 댓글목록


    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.nickname = post.getUser().getNickname();
        this.username = post.getUser().getUsername();
        this.title = post.getTitle();
        this.contents = post.getContents();
        this.likeCnt = post.getLikeCnt();
        this.viewCount = post.getViewCount();
        this.postImageUrl = post.getPostImageUrl();
        this.createdAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();

    }
}
