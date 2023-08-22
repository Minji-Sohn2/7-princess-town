package com.example.princesstown.controller.comment;

import com.example.princesstown.dto.comment.ReplyRequestDto;
import com.example.princesstown.dto.comment.RestApiResponseDto;
import com.example.princesstown.exception.TokenNotValidateException;
import com.example.princesstown.security.user.UserDetailsImpl;
import com.example.princesstown.service.comment.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReplyController {
    private final ReplyService replyService;

    @GetMapping("/posts/{postId}/comments/{commentId}/reply")
    public ResponseEntity<RestApiResponseDto> getReplys(
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        return replyService.getReplys(postId, commentId);
    }

    // 댓글 작성
    @PostMapping("/posts/{postId}/comments/{commentId}/reply")
    public ResponseEntity<RestApiResponseDto> createReplys(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody ReplyRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        return replyService.createReplys(postId, commentId, requestDto, userDetails.getUser());
    }

    // 댓글 수정
    @PutMapping("/posts/{postId}/comments/{commentId}/reply/{replyId}")
    public ResponseEntity<RestApiResponseDto> updateReplys(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @PathVariable Long replyId,
            @RequestBody ReplyRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        return replyService.updateReplys(postId, commentId, replyId, requestDto, userDetails.getUser());
    }

    // 댓글 삭제
    @DeleteMapping("/posts/{postId}/comments/{commentId}/reply/{replyId}")
    public ResponseEntity<RestApiResponseDto> deleteReplys(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @PathVariable Long replyId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        return replyService.deleteReplys(postId, commentId, replyId, userDetails.getUser());
    }

    @GetMapping("/posts/{postId}/comments/{commentId}/reply/likes")
    public ResponseEntity<RestApiResponseDto> getLikes(
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        return replyService.getLikes(postId, commentId);
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/reply/{replyId}/likes")
    public ResponseEntity<RestApiResponseDto> createLikes(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @PathVariable Long replyId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        return replyService.createLikes(postId, commentId, replyId, userDetails.getUser());
    }

    @PutMapping("/posts/{postId}/comments/{commentId}/reply/{replyId}/likes")
    public ResponseEntity<RestApiResponseDto> deleteLikes(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @PathVariable Long replyId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        return replyService.deleteLikes(postId, commentId, replyId, userDetails.getUser());
    }
}
