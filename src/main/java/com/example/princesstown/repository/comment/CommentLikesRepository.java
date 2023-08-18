package com.example.princesstown.repository.comment;

import com.example.princesstown.entity.CommentLikes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentLikesRepository extends JpaRepository<CommentLikes, Long> {
    Optional<CommentLikes> findByCommentIdAndUserUserId(Long commentId, Long userId);

    List<CommentLikes> findAllByPostId(Long postId);
}
