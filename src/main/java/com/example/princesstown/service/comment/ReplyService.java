package com.example.princesstown.service.comment;

import com.example.princesstown.dto.comment.CommentRequestDto;
import com.example.princesstown.dto.comment.RestApiResponseDto;
import com.example.princesstown.entity.ReplyLikes;
import com.example.princesstown.entity.User;
import com.example.princesstown.repository.comment.CommentRepository;
import com.example.princesstown.repository.comment.ReplyLikesRepository;
import com.example.princesstown.repository.comment.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final ReplyLikesRepository replyLikesRepository;

    public ResponseEntity<RestApiResponseDto> getReplys(Long postId, Long commentId, CommentRequestDto requestDto) {
        try {

        } catch (IllegalArgumentException e) {

        }
        return null;
    }

    public ResponseEntity<RestApiResponseDto> createReplys(Long postId, Long commentId, CommentRequestDto requestDto, User user) {
        try {

        } catch (IllegalArgumentException e) {

        }
        return null;
    }

    public ResponseEntity<RestApiResponseDto> updateReplys(Long postId, Long commentId, Long replyId, CommentRequestDto requestDto, User user) {
        try {

        } catch (IllegalArgumentException e) {

        }
        return null;
    }

    public ResponseEntity<RestApiResponseDto> deleteReplys(Long postId, Long commentId, Long replyId, CommentRequestDto requestDto, User user) {
        try {

        } catch (IllegalArgumentException e) {

        }
        return null;
    }

    public ResponseEntity<RestApiResponseDto> liketogle(Long postId, Long commentId, Long replyId, CommentRequestDto requestDto, User user) {
        try {
            ReplyLikes replyLikes = replyLikesRepository.findByReplyIdAndCommentIdAndPostIdAndUserId(replyId, commentId, postId, userId);
        } catch (IllegalArgumentException e) {

        }
        return null;
    }
}
