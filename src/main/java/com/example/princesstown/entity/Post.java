package com.example.princesstown.entity;

import com.example.princesstown.dto.request.PostRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor
@Table(name = "posts")
public class Post extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column(length = 500)
    private String contents;

    @Column(name = "like_count")
    private Long likeCount;

    @Column(name = "view_count", columnDefinition = "int default 0", nullable = false)
    private int viewCount;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_userId", nullable = false)
    private User user;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    // post를 연관관계의 주인으로 설정. post 엔티티 제거시 연관된 comment 제거.
//    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
//    private List<Comment> commentList = new ArrayList<>();

    // post를 연관관계의 주인으로 설정. post 엔티티 제거시 연관된 like 제거.
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Like> likeList = new ArrayList<>();

    public Post(PostRequestDto postRequestDto, User user, Board board, Long likeCount){
        this.user = user;
        this.board = board;
        this.likeCount = likeCount;
        this.title = postRequestDto.getTitle();
        this.contents = postRequestDto.getContents();
    }

    public void update(PostRequestDto postRequestDto){
        this.title = postRequestDto.getTitle();
        this.contents = postRequestDto.getContents();
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }
}