package com.example.princesstown.repository.comment;

import com.example.princesstown.entity.CommentLikes;
import com.example.princesstown.entity.ReplyLikes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikesRepository extends JpaRepository<CommentLikes, Long> {
    Optional<CommentLikes> findByCommentId(Long commentId);
}
