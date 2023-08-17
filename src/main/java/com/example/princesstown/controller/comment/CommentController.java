package com.example.princesstown.controller.comment;

import com.example.princesstown.dto.comment.CommentRequestDto;
import com.example.princesstown.dto.comment.RestApiResponseDto;
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

    // 댓글 작성
//    @PostMapping("/posts/{postId}/coomments")
//    public ResponseEntity<RestApiResponseDto> createComment(
//            @PathVariable Long postId,
//            @RequestBody CommentRequestDto requestDto,
//            @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        this.tokenValidate(userDetails);
//
//        return commentService.createComment(postId, requestDto, userDetails.getUser());
//    }


}
