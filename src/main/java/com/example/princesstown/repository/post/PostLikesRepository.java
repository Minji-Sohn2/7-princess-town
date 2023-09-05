package com.example.princesstown.repository.post;

import com.example.princesstown.entity.Post;
import com.example.princesstown.entity.PostLikes;
import com.example.princesstown.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostLikesRepository extends JpaRepository<PostLikes, Long> {

    Optional<PostLikes> findByPostIdAndUserUserId(Long postId, Long userId);

    PostLikes findByUserAndPost(User user, Post post);

    List<PostLikes> findAllByPostId(Long postId);

    PostLikes findByUserUserIdAndPostId(Long id, Long id1);
}
