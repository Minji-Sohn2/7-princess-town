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

    // 전체 게시글 조회
    List<Post> findAllByOrderByCreatedAtDesc();

    List<Post> findAllByUserOrderByCreatedAtDesc(User user);

    //특정 게시판 게시글 전체 조회
    List<Post> findByBoardIdOrderByCreatedAtDesc(Long boardId);

    //제목으로 검색
    List<Post> findAllByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String title);

    //내용으로 검색
    List<Post> findAllByContentsContainingIgnoreCaseOrderByCreatedAtDesc(String contents);

    //제목과 내용으로 검색
    List<Post> findAllByTitleContainingIgnoreCaseOrContentsContainingIgnoreCaseOrderByCreatedAtDesc(String title, String contents);

    //top10 인기검색어
    @Query("SELECT p FROM Post p WHERE p.likeCount > 0 ORDER BY p.likeCount DESC")
    List<Post> findTop10LikedPostsWithDuplicates();

    @Transactional
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :postId")
    int incrementViewCount(@Param("postId") Long postId);

}
