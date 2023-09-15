package com.example.princesstown.dto.response;

import com.example.princesstown.entity.Post;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

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
    private String createdAt; // 게시글 생성시간
    private String modifiedAt; // 게시글 수정시간
    private Double latitude;
    private Double longitude;
    private String boards;
    private Long boardId;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.nickname = post.getUser().getNickname();
        this.username = post.getUser().getUsername();
        this.title = post.getTitle();
        this.contents = post.getContents();
        this.likeCnt = post.getLikeCnt();
        this.viewCount = post.getViewCount();
        this.postImageUrl = post.getPostImageUrl();
        this.boards = post.getBoard().getTitle();
        this.boardId = post.getBoard().getId();
        this.createdAt = post.getCreatedAt().format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm"));
        this.modifiedAt = post.getModifiedAt().format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm"));
        if (post.getLocation() != null) {
            this.latitude = post.getLocation().getLatitude();
            this.longitude = post.getLocation().getLongitude();
        } else {
            this.latitude = null;
            this.longitude = null;
        }
    }
}
