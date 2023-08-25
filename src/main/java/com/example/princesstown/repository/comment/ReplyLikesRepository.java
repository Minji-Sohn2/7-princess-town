package com.example.princesstown.repository.comment;

import com.example.princesstown.entity.ReplyLikes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReplyLikesRepository extends JpaRepository<ReplyLikes, Long> {

    Optional<ReplyLikes> findByReplyIdAndUserUserId(Long replyId, Long userId);

    List<ReplyLikes> findAllByCommentId(Long commentId);

    List<ReplyLikes> findAllByCommentIdAndReplyId(Long commentId,Long replyId);
}
