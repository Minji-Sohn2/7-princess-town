package com.example.princesstown.service.comment;

import com.example.princesstown.controller.comment.CommentController;
import com.example.princesstown.dto.comment.CommentLikesRequestDto;
import com.example.princesstown.dto.comment.CommentLikesResponseDto;
import com.example.princesstown.dto.comment.CommentRequestDto;
import com.example.princesstown.dto.comment.RestApiResponseDto;
import com.example.princesstown.entity.Comment;
import com.example.princesstown.entity.CommentLikes;
import com.example.princesstown.entity.Post;
import com.example.princesstown.entity.User;
import com.example.princesstown.repository.comment.CommentLikesRepository;
import com.example.princesstown.repository.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentController commentController;
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

    public ResponseEntity<RestApiResponseDto> liketogle(Long commentId, Long postId, CommentLikesRequestDto requestDto, User user) {
        try {
            Long userId = user.getId();

            commentRepository.findByPostId(postId).orElseThrow(
                    () -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));

            CommentLikes commentLikes = commentLikesRepository.findByCommentIdAndUserId(commentId, userId)
                    .orElseThrow(
                            () -> new IllegalArgumentException("해당 좋아요가 존재하지 않습니다."));

            Long writerId = commentLikes.getUser().getId();
            if (!writerId.equals(userId)) {
                throw new IllegalArgumentException("잘못된 접근입니다.");
            }

            commentLikes.setLikes(true);
            commentLikes.update(requestDto);

            return this.resultResponse(HttpStatus.OK, "좋아요 클릭", new CommentLikesResponseDto(commentLikes));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RestApiResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    private ResponseEntity<RestApiResponseDto> resultResponse(HttpStatus status, String message, Object result) {
        RestApiResponseDto restApiResponseDto = new RestApiResponseDto(status.value(), message, result);
        return new ResponseEntity<>(
                restApiResponseDto,
                status
        );
    }
}
