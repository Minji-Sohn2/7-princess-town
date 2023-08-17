package com.example.princesstown.entity;

import com.example.princesstown.dto.comment.ReplyLikesRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

@Getter
@Setter
@Entity
@DynamicInsert
@NoArgsConstructor
@Table(name = "replylikes")
public class ReplyLikes extends TimeStamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private boolean likes;

    @ManyToOne
    @JoinColumn(name = "reply_id")
    private Reply reply;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @PostPersist
    @PostUpdate
    public void afterLikeChange() {
        if (likes) {
            reply.setLikeCnt(reply.getLikeCnt() + 1);
        } else {
            reply.setLikeCnt(reply.getLikeCnt() - 1);
        }
    }

    public void update(ReplyLikesRequestDto requestDto) {
        this.likes = requestDto.isLikes();
    }
}
