package com.example.princesstown.repository.post;

import com.example.princesstown.entity.Post;
import com.example.princesstown.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 게시글을 생성된 시간순으로 내림차순해서 가져옴
    List<Post> findAllByOrderByCreatedAtDesc();

    List<Post> findAllByUserOrderByCreatedAtDesc(User user);

    List<Post> findByBoardIdOrderByCreatedAtDesc(Long boardId);

    List<Post> findAllByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String title);

    //top10 인기검색어
    @Query("SELECT p FROM Post p WHERE p.likeCount > 0 ORDER BY p.likeCount DESC")
    List<Post> findTop10LikedPostsWithDuplicates();

    @Transactional
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :postId")
    int incrementViewCount(@Param("postId") Long postId);

}
