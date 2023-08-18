package com.example.princesstown.controller.comment;

import com.example.princesstown.dto.comment.CommentRequestDto;
import com.example.princesstown.dto.comment.RestApiResponseDto;
import com.example.princesstown.exception.TokenNotValidateException;
import com.example.princesstown.security.user.UserDetailsImpl;
import com.example.princesstown.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    // 댓글 조회
    @GetMapping("/posts/{postId}/coomments")
    public ResponseEntity<RestApiResponseDto> getComments(
            @PathVariable Long postId,
            @RequestBody CommentRequestDto requestDto
    ) {
        return commentService.getComments(postId, requestDto);
    }

     // 댓글 작성
    @PostMapping("/posts/{postId}/coomments")
    public ResponseEntity<RestApiResponseDto> createComments(
            @PathVariable Long postId,
            @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        this.tokenValidate(userDetails);

        return commentService.createComments(postId, requestDto, userDetails.getUser());
    }

    // 댓글 수정
    @PutMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<RestApiResponseDto> updateComments(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        this.tokenValidate(userDetails);

        return commentService.updateComments(postId, commentId, requestDto, userDetails.getUser());
    }

    // 댓글 삭제
    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<RestApiResponseDto> deleteComments(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        this.tokenValidate(userDetails);

        return commentService.deleteComments(postId, commentId, requestDto, userDetails.getUser());
    }

    // 좋아요 조회
    @GetMapping("/posts/{postId}/comments/{commentId}/likes")
    public ResponseEntity<RestApiResponseDto> getLikes(
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        return commentService.getLikes(postId, commentId);
    }

    // 좋아요 추가
    @PostMapping("/posts/{postId}/comments/{commentId}/likes")
    public ResponseEntity<RestApiResponseDto> createLikes(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @PathVariable Long replyId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        this.tokenValidate(userDetails);

        return commentService.createLikes(postId, commentId, replyId, userDetails.getUser());
    }

    // 좋아요 취소
    @PutMapping("/posts/{postId}/comments/{commentId}/likes")
    public ResponseEntity<RestApiResponseDto> deleteLikes(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        this.tokenValidate(userDetails);

        return commentService.deleteLikes(postId, commentId, userDetails.getUser());
    }

    public void tokenValidate(UserDetailsImpl userDetails) {
         try {
             userDetails.getUser();
         } catch (Exception e) {
             throw new TokenNotValidateException("토큰이 유효하지 않습니다.");
         }
    }
}
