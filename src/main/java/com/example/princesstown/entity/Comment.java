package com.example.princesstown.entity;

import com.example.princesstown.dto.comment.CommentRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@DynamicInsert
@NoArgsConstructor
@Table(name = "comments")
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column
    private String emoji;

    @ColumnDefault("0")
    private Long likeCnt;


    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_userId")
    private User user;

    @OneToMany( mappedBy = "comment", cascade = CascadeType.ALL)
    private List<Reply> ReplyList= new ArrayList<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<CommentLikes> commentLikesList = new ArrayList<>();

    public Comment(CommentRequestDto requestDto, Post post, User user) {
        this.id = getId();
        this.content = requestDto.getContent();
        this.emoji = requestDto.getEmoji();
        this.likeCnt = getLikeCnt();
        this.post = post;
        this.user = user;
    }
}
