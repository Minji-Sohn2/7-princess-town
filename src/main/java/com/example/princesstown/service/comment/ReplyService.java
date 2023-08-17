package com.example.princesstown.service.comment;

import com.example.princesstown.dto.comment.*;
import com.example.princesstown.entity.ReplyLikes;
import com.example.princesstown.entity.User;
import com.example.princesstown.repository.comment.CommentRepository;
import com.example.princesstown.repository.comment.ReplyLikesRepository;
import com.example.princesstown.repository.comment.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    public ResponseEntity<RestApiResponseDto> createLike(Long postId, Long commentId, Long replyId, User user) {
        try {
            replyRepository.findByPostIdAndCommentId(postId, commentId).orElseThrow(
                    () -> new IllegalArgumentException("해당 게시물이나 댓글이 존재하지 않습니다."));

            ReplyLikes replyLikes = replyLikesRepository.findByReplyId(replyId)
                    .orElseThrow(
                            () -> new IllegalArgumentException("해당 답글이 존재하지 않습니다."));

            Long writerId = replyLikes.getUser().getId();
            Long loginId = user.getId();
            if (!writerId.equals(loginId)) {
                throw new IllegalArgumentException("다른 사용자의 요청입니다.");
            }

            if (replyLikes.isLikes()) {
                throw new IllegalArgumentException("이미 좋아요가 눌러져있는 상태입니다.");
            } else {
                replyLikes.setLikes(true);
                replyLikesRepository.save(replyLikes);
            }

            return this.resultResponse(HttpStatus.CREATED, "좋아요 생성", new ReplyLikesResponseDto(replyLikes));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RestApiResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    public ResponseEntity<RestApiResponseDto> deleteLike(Long postId, Long commentId, Long replyId, User user) {
        try {
            replyRepository.findByPostIdAndCommentId(postId, commentId).orElseThrow(
                    () -> new IllegalArgumentException("해당 게시물이나 댓글이 존재하지 않습니다."));

            Long userId = user.getId();
            ReplyLikes replyLikes = replyLikesRepository.findByReplyId(replyId)
                    .orElseThrow(
                            () -> new IllegalArgumentException("해당 답글이 존재하지 않습니다."));

            Long writerId = replyLikes.getUser().getId();
            if (!writerId.equals(userId)) {
                throw new IllegalArgumentException("잘못된 접근입니다.");
            }

            if (!replyLikes.isLikes()) {
                throw new IllegalArgumentException("이미 취소된 좋아요입니다.");
            } else {
                replyLikes.setLikes(false);
                replyLikesRepository.save(replyLikes);
            }


            return this.resultResponse(HttpStatus.CREATED, "좋아요 취소", new ReplyLikesResponseDto(replyLikes));

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
