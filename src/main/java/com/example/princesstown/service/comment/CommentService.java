package com.example.princesstown.service.comment;

import com.example.princesstown.dto.comment.*;
import com.example.princesstown.entity.*;
import com.example.princesstown.repository.comment.CommentLikesRepository;
import com.example.princesstown.repository.comment.CommentRepository;
import com.example.princesstown.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentLikesRepository commentLikesRepository;

    // 댓글 가져오기
    public ResponseEntity<RestApiResponseDto> getComments(Long postId, CommentRequestDto requestDto) {

        try {
            getPostId(postId);

            List<Comment> commentsList = commentRepository.findAllByPostId(postId);

            List<CommentResponseDto> commentResponseDtoList = commentsList.stream()
                    .map(CommentResponseDto::new)
                    .toList();

            return this.resultResponse(HttpStatus.OK, "댓글 조회", commentResponseDtoList);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RestApiResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    // 댓글 생성
    public ResponseEntity<RestApiResponseDto> createComments(Long postId, CommentRequestDto requestDto, User user) {
        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));

            Comment comment = new Comment(requestDto, post, user);

            commentRepository.save(comment);
            return this.resultResponse(HttpStatus.CREATED, "댓글 생성", new CommentResponseDto(comment));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RestApiResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    // 댓글 수정
    public ResponseEntity<RestApiResponseDto> updateComments(Long postId, Long commentId, CommentRequestDto requestDto, User user) {
        try {
            getPostId(postId);

            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

            commentsValid(comment, user);

            comment.update(requestDto);

            return this.resultResponse(HttpStatus.OK, "댓글 수정", new CommentResponseDto(comment));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RestApiResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    // 댓글 삭제
    public ResponseEntity<RestApiResponseDto> deleteComments(Long postId, Long commentId, CommentRequestDto requestDto, User user) {
        try {
            postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));

            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

            commentsValid(comment, user);

            commentRepository.delete(comment);

            return this.resultResponse(HttpStatus.OK, "댓글 삭제", new CommentResponseDto(comment));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RestApiResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    // 좋아요 가져오기
    public ResponseEntity<RestApiResponseDto> getLikes(Long postId, Long commentId) {
        try {
            getPostId(postId);

            List<CommentLikes> likesList = commentLikesRepository.findAllByCommentId(commentId);

            List<CommentLikesResponseDto> commentLikesResponseDtoList = likesList.stream()
                    .map(CommentLikesResponseDto :: new)
                    .toList();

            return this.resultResponse(HttpStatus.OK, "좋아요 조회", commentLikesResponseDtoList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RestApiResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    // 좋아요 생성
    public ResponseEntity<RestApiResponseDto> createLikes(Long commentId, Long postId, User user) {
        try {
            getPostId(postId);

            CommentLikes commentLikes = commentLikesRepository.findByCommentId(commentId)
                    .orElseThrow(
                            () -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

            likesValid(commentLikes, user);

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

    // 좋아요 취소
    public ResponseEntity<RestApiResponseDto> deleteLikes(Long commentId, Long postId, User user) {
        try {
            getPostId(postId);

            CommentLikes commentLikes = commentLikesRepository.findByCommentId(commentId)
                    .orElseThrow(
                            () -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

            likesValid(commentLikes, user);

            if (!commentLikes.isLikes()) {
                throw new IllegalArgumentException("이미 취소된 좋아요입니다.");
            } else {
                commentLikes.setLikes(false);
                commentLikesRepository.save(commentLikes);
            }

            return this.resultResponse(HttpStatus.OK, "좋아요 취소", new CommentLikesResponseDto(commentLikes));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RestApiResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    // 상태코드, 메세지, json 반환
    private ResponseEntity<RestApiResponseDto> resultResponse(HttpStatus status, String message, Object result) {
        RestApiResponseDto restApiResponseDto = new RestApiResponseDto(status.value(), message, result);
        return new ResponseEntity<>(
                restApiResponseDto,
                status
        );
    }

    // 게시물Id 가져오는 메소드 분리
    private void getPostId(Long postId) {
        commentRepository.findByPostId(postId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시물이나 댓글이 존재하지 않습니다."));
    }

    // 댓글 사용자 검증
    private void commentsValid(Comment comment, User user) {
        Long writerId = comment.getUser().getId();
        Long loginId = user.getId();
        if (!writerId.equals(loginId)) {
            throw new IllegalArgumentException("작성자만 수정 혹은 삭제가 가능합니다.");
        }
    }

    // 좋아요 사용자 검증
    private void likesValid(CommentLikes commentLikes, User user) {
        Long writerId = commentLikes.getUser().getId();
        Long loginId = user.getId();
        if (!writerId.equals(loginId)) {
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }
    }
}
