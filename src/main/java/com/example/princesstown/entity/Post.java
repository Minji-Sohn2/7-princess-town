package com.example.princesstown.entity;

import com.example.princesstown.dto.request.PostRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
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

    @ColumnDefault("0")
    private Long likeCnt;

    @Column(name = "view_count", columnDefinition = "int default 0", nullable = false)
    private int viewCount;

    @Column // 이미지 URL 저장을 위한 컬럼 추가
    private String postImageUrl;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_userId", nullable = false)
    private User user;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne
    @JoinColumn(name = "location_locationId")
    private Location location;


    // post를 연관관계의 주인으로 설정. post 엔티티 제거시 연관된 comment 제거.
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Comment> commentList = new ArrayList<>();

    // post를 연관관계의 주인으로 설정. post 엔티티 제거시 연관된 like 제거.
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<PostLikes> postLikesList = new ArrayList<>();

    public Post(PostRequestDto postRequestDto, User user, Board board, Long likeCnt, String postImageUrl){
        this.user = user;
        this.board = board;
        this.likeCnt = likeCnt;
        this.title = postRequestDto.getTitle();
        this.contents = postRequestDto.getContents();
        this.postImageUrl = postImageUrl;
        this.location = user.getLocation();
    }

    public void update(PostRequestDto postRequestDto){
        this.title = postRequestDto.getTitle();
        this.contents = postRequestDto.getContents();
    }

    public void setImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }
}