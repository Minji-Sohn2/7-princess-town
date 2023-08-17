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

    public ResponseEntity<RestApiResponseDto> liketogle(Long postId, Long commentId, Long replyId, ReplyLikesRequestDto requestDto, User user) {
        try {

            replyRepository.findByPostId(postId).orElseThrow(
                    () -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));

            replyRepository.findByCommentId(commentId).orElseThrow(
                    () -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));


            Long userId = user.getId();
            ReplyLikes replyLikes = replyLikesRepository.findByReplyIdAndUserId(replyId, userId)
                    .orElseThrow(
                            () -> new IllegalArgumentException("해당 좋아요가 존재하지 않습니다."));

            Long writerId = replyLikes.getUser().getId();
            if (!writerId.equals(userId)) {
                throw new IllegalArgumentException("잘못된 접근입니다.");
            }

            replyLikes.setLikes(true);
            replyLikes.update(requestDto);
            return this.resultResponse(HttpStatus.OK, "좋아요 클릭", new ReplyLikesResponseDto(replyLikes));
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
