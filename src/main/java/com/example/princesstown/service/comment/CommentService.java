package com.example.princesstown.service.comment;

import com.example.princesstown.dto.comment.CommentRequestDto;
import com.example.princesstown.dto.comment.RestApiResponseDto;
import com.example.princesstown.entity.Comment;
import com.example.princesstown.entity.CommentLikes;
import com.example.princesstown.entity.User;
import com.example.princesstown.repository.comment.CommentLikesRepository;
import com.example.princesstown.repository.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikesRepository commentLikesRepository;

    public ResponseEntity<RestApiResponseDto> getComments(Long postId, CommentRequestDto requestDto) {
        try {

        } catch (IllegalArgumentException e) {

        }
        return null;
    }

    public ResponseEntity<RestApiResponseDto> createComments(Long postId, CommentRequestDto requestDto, User user) {
        try {

        } catch (IllegalArgumentException e) {

        }
        return null;
    }

    public ResponseEntity<RestApiResponseDto> updateComments(Long postId, CommentRequestDto requestDto, User user) {
        try {

        } catch (IllegalArgumentException e) {

        }
        return null;
    }

    public ResponseEntity<RestApiResponseDto> deleteComments(Long postId, CommentRequestDto requestDto, User user) {
        try {

        } catch (IllegalArgumentException e) {

        }
        return null;
    }

    public ResponseEntity<RestApiResponseDto> liketogle(Long commentId, Long postId, CommentRequestDto requestDto, User user) {
        try {
            Long userId = user.getId();
            CommentLikes commentLikes = commentLikesRepository.findByCommentIdAndPostIdAndUserId(commentId, postId, userId)
                    .orElseThrow(
                            () -> new IllegalArgumentException("해당 좋아요가 존재하지 않습니다."));
        } catch (IllegalArgumentException e) {

        }
        return null;
    }
}
