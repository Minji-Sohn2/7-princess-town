package com.example.princesstown.service.comment;

import com.example.princesstown.dto.comment.CommentLikesRequestDto;
import com.example.princesstown.dto.comment.CommentLikesResponseDto;
import com.example.princesstown.dto.comment.CommentRequestDto;
import com.example.princesstown.dto.comment.RestApiResponseDto;
import com.example.princesstown.entity.CommentLikes;
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

    public ResponseEntity<RestApiResponseDto> createLikes(Long commentId, Long postId, User user) {
        try {
            commentRepository.findByPostId(postId).orElseThrow(
                    () -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));

            CommentLikes commentLikes = commentLikesRepository.findByCommentId(commentId)
                    .orElseThrow(
                            () -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

            Long writerId = commentLikes.getUser().getId();
            Long loginId = user.getId();
            if (!writerId.equals(loginId)) {
                throw new IllegalArgumentException("다른 사용자의 요청입니다.");
            }

            if (commentLikes.isLikes()) {
                throw new IllegalArgumentException("이미 좋아요가 눌러져있는 상태입니다.");
            } else {
                commentLikes.setLikes(true);
                commentLikesRepository.save(commentLikes);
            }

            return this.resultResponse(HttpStatus.CREATED, "좋아요 생성", new CommentLikesResponseDto(commentLikes));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RestApiResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    public ResponseEntity<RestApiResponseDto> deleteLikes(Long commentId, Long postId, User user) {
        try {

            commentRepository.findByPostId(postId).orElseThrow(
                    () -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));

            CommentLikes commentLikes = commentLikesRepository.findByCommentId(commentId)
                    .orElseThrow(
                            () -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

            Long writerId = commentLikes.getUser().getId();
            Long loginId = user.getId();
            if (!writerId.equals(loginId)) {
                throw new IllegalArgumentException("잘못된 접근입니다.");
            }

            if (!commentLikes.isLikes()) {
                throw new IllegalArgumentException("이미 취소된 좋아요입니다.");
            } else {
                commentLikes.setLikes(false);
                commentLikesRepository.save(commentLikes);
            }

            return this.resultResponse(HttpStatus.CREATED, "좋아요 취소", new CommentLikesResponseDto(commentLikes));
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
