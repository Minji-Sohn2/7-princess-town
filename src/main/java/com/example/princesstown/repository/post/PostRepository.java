package com.example.princesstown.repository.post;

import com.example.princesstown.entity.Post;
import com.example.princesstown.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 게시글을 생성된 시간순으로 내림차순해서 가져옴
    List<Post> findAllByOrderByCreatedAtDesc();

    List<Post> findAllByUserOrderByCreatedAtDesc(User user);

    List<Post> findByBoardIdOrderByCreatedAtDesc(Long boardId);

}
