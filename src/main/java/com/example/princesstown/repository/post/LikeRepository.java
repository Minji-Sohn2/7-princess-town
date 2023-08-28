package com.example.princesstown.repository.post;

import com.example.princesstown.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
//    Like findByUserIdAndCommentId(Long id, Long commentId);

//    List<Like> findByCommentId(Long commentId);

//    List<Like> findByPostIdAndCommentId(Long blogId, Long commentId);

    List<Like> findByPostId(Long id);

    Like findByUserUserIdAndPostId(Long id, Long id1);
}
