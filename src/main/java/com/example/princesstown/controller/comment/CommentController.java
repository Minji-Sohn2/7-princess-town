package com.example.princesstown.controller.comment;

import com.example.princesstown.dto.comment.CommentRequestDto;
import com.example.princesstown.dto.comment.RestApiResponseDto;
import com.example.princesstown.security.user.UserDetailsImpl;
import com.example.princesstown.service.comment.CommentService;
import jakarta.validation.Valid;
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
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<RestApiResponseDto> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return commentService.getComments(postId, page, size);
    }

    @GetMapping("/posts/{postId}/commentlist")
    public ResponseEntity<RestApiResponseDto> getAllComments(
            @PathVariable Long postId
    ) {
        return commentService.getAllComments(postId);
    }

     // 댓글 작성
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<RestApiResponseDto> createComments(
            @PathVariable Long postId,
            @RequestBody @Valid CommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        return commentService.createComments(postId, requestDto, userDetails.getUser());
    }

    // 댓글 수정
    @PutMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<RestApiResponseDto> updateComments(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody @Valid CommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        return commentService.updateComments(postId, commentId, requestDto, userDetails.getUser());
    }

    // 댓글 삭제
    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<RestApiResponseDto> deleteComments(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        return commentService.deleteComments(postId, commentId, userDetails.getUser());
    }

    // 좋아요 조회
    @GetMapping("/posts/{postId}/comments/likes")
    public ResponseEntity<RestApiResponseDto> getLikes(
            @PathVariable Long postId
    ) {
        return commentService.getLikes(postId);
    }

    // 좋아요 추가
    @PostMapping("/posts/{postId}/comments/{commentId}/likes")
    public ResponseEntity<RestApiResponseDto> createLikes(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        return commentService.createLikes(postId, commentId, userDetails.getUser());
    }

    // 좋아요 취소
    @PutMapping("/posts/{postId}/comments/{commentId}/likes")
    public ResponseEntity<RestApiResponseDto> deleteLikes(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        return commentService.deleteLikes(postId, commentId, userDetails.getUser());
    }

    @GetMapping("/nickname")
    public ResponseEntity<RestApiResponseDto> getNickname(
            @RequestParam String username
    ) {
        return commentService.getNickname(username);
    }
}
