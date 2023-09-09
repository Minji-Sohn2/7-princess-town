package com.example.princesstown.repository.post;

import com.example.princesstown.entity.Location;
import com.example.princesstown.entity.Post;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collections;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 전체 게시글 조회
    List<Post> findAllByOrderByCreatedAtDesc();

    // 전체게시글 페이지로 반환
    List<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    //특정 게시판 게시글 전체 조회
    List<Post> findByBoardIdOrderByCreatedAtDesc(Long boardId);

    //제목으로 검색
    List<Post> findAllByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String title);

    //내용으로 검색
    List<Post> findAllByContentsContainingIgnoreCaseOrderByCreatedAtDesc(String contents);

    //제목과 내용으로 검색
    List<Post> findAllByTitleContainingIgnoreCaseOrContentsContainingIgnoreCaseOrderByCreatedAtDesc(String title, String contents);

    //top10 인기검색어
    @Query("SELECT p FROM Post p WHERE p.likeCnt > 0 ORDER BY p.likeCnt DESC")
    List<Post> findTop10LikedPostsWithDuplicates();

    @Transactional
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :postId")
    int incrementViewCount(@Param("postId") Long postId);

    //     위치 범위 내의 게시물을 찾는 메서드
    List<Post> findByLocationIn(List<Location> locations);

    List<Post> findByLocationLatitudeBetweenAndLocationLongitudeBetween(Double minLat, Double maxLat, Double minLon, Double maxLon);

    // 위치로 게시물을 찾는 메서드
    default List<Post> findByLocation(Location location) {
        return findByLocationIn(Collections.singletonList(location));
    }

}
