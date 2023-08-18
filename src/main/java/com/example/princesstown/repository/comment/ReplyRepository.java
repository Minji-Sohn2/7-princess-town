package com.example.princesstown.repository.comment;

import com.example.princesstown.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findAllByPostIdAndCommentId(Long postId, Long commentId);
}
