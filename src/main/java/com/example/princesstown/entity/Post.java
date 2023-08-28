package com.example.princesstown.entity;

import com.example.princesstown.dto.request.PostRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "post")
@NoArgsConstructor
public class Post extends Timestamped { // 상속받아서 createdAt, modifiedAt column 가져옴
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 3000)
    private String contents;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Integer postLikeCount;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;


//    @OneToMany( mappedBy = "post",fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    private List<Comment> commentList = new ArrayList<>();


    public Post(PostRequestDto requestDto, User user) {
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContents();
        this.nickname = user.getNickname();
        this.username = user.getUsername();
        this.postLikeCount = 0;
    }

    public void updatePost(PostRequestDto postRequestDto) {
        this.title = postRequestDto.getTitle();
        this.contents = postRequestDto.getContents();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
    }

    public void setPostLikedCount(Integer postLikedCount) {
        this.postLikeCount = postLikedCount;
    }
}