package com.example.princesstown.entity;
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
public class ReplyLikes extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private boolean likes;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "reply_id")
    private Reply reply;

    @ManyToOne
    @JoinColumn(name = "user_userId")
    private User user;

    public ReplyLikes(boolean likes, Comment comment,Reply reply, User user) {
        this.likes = likes;
        this.comment = comment;
        this.reply = reply;
        this.user = user;
    }
}
