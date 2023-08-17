package com.example.princesstown.controller.comment;

import com.example.princesstown.dto.comment.CommentRequestDto;
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

    @GetMapping("/posts/{postid}/comments/{commentsid}/reply")
    public ResponseEntity<RestApiResponseDto> getReplys(
            @PathVariable Long postId,
            @RequestBody CommentRequestDto requestDto
    ) {
        return replyService.getReplys(postId, requestDto);
    }

    // 댓글 작성
    @PostMapping("/posts/{postId}/comments/{commentId}/reply")
    public ResponseEntity<RestApiResponseDto> createReplys(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        this.tokenValidate(userDetails);

        return replyService.createReplys(postId, commentId, requestDto, userDetails.getUser());
    }

    // 댓글 수정
    @PutMapping("/posts/{postId}/comments/{commentId}/reply/{replyId}")
    public ResponseEntity<RestApiResponseDto> updateReplys(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @PathVariable Long replyId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        this.tokenValidate(userDetails);

        return replyService.updateReplys(postId, commentId, replyId, userDetails.getUser());
    }

    // 댓글 삭제
    @DeleteMapping("/posts/{postId}/comments/{commentId}/reply/{replyId}")
    public ResponseEntity<RestApiResponseDto> deleteReplys(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @PathVariable Long replyId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        this.tokenValidate(userDetails);

        return replyService.deleteReplys(postId, commentId, replyId, userDetails.getUser());
    }

    @PutMapping("/posts/{postId}/comments/{commentId}/reply/{replyId}")
    public ResponseEntity<RestApiResponseDto> likestogle(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @PathVariable Long replyId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        this.tokenValidate(userDetails);

        return replyService.liketogle(postId, commentId, replyId, userDetails.getUser());
    }

    public void tokenValidate(UserDetailsImpl userDetails) {
        try {
            userDetails.getUser();
        } catch (Exception e) {
            throw new TokenNotValidateException("토큰이 유효하지 않습니다.");
        }
    }
}
