package com.example.princesstown.repository.comment;

import com.example.princesstown.entity.ReplyLikes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReplyLikesRepository extends JpaRepository<ReplyLikes, Long> {
    Optional<ReplyLikes> findByReplyId(Long replyId);

    List<ReplyLikes> findAllByReplyId(Long replyId);
}
