package com.example.princesstown.repository.comment;

import com.example.princesstown.entity.Reply;
import com.example.princesstown.entity.ReplyLikes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findAllByPostIdAndCommentIdOrderByCreatedAtAsc(Long postId, Long commentId);
}
