package com.example.princesstown.service.comment;

import com.example.princesstown.dto.comment.CommentLikesResponseDto;
import com.example.princesstown.dto.comment.CommentRequestDto;
import com.example.princesstown.dto.comment.CommentResponseDto;
import com.example.princesstown.dto.comment.RestApiResponseDto;
import com.example.princesstown.entity.Comment;
import com.example.princesstown.entity.CommentLikes;
import com.example.princesstown.entity.Post;
import com.example.princesstown.entity.User;
import com.example.princesstown.repository.comment.CommentLikesRepository;
import com.example.princesstown.repository.comment.CommentRepository;
import com.example.princesstown.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentLikesRepository commentLikesRepository;

    // 댓글 가져오기
    public ResponseEntity<RestApiResponseDto> getComments(Long postId) {
            List<Comment> commentsList = commentRepository.findAllByPostIdOrderByCreatedAtAsc(postId);

            List<CommentResponseDto> commentResponseDtoList = commentsList.stream()
                    .map(CommentResponseDto::new)
                    .toList();

            return this.resultResponse(HttpStatus.OK, "댓글 조회", commentResponseDtoList);
    }

    // 댓글 생성
    public ResponseEntity<RestApiResponseDto> createComments(Long postId, CommentRequestDto requestDto, User user) {
        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));

            Comment comment = new Comment(requestDto, post, user);

            comment.setLikeCnt(0L);

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

            comment.setContent(requestDto.getContent());
            comment.setEmoji(requestDto.getEmoji());

            commentRepository.save(comment);

            return this.resultResponse(HttpStatus.OK, "댓글 수정", new CommentResponseDto(comment));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RestApiResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    // 댓글 삭제
    public ResponseEntity<RestApiResponseDto> deleteComments(Long postId, Long commentId, User user) {
        try {
            getPostId(postId);

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
    public ResponseEntity<RestApiResponseDto> getLikes(Long postId) {
        try {
            getPostId(postId);

            List<CommentLikes> likesList = commentLikesRepository.findAllByPostId(postId);

            List<CommentLikesResponseDto> commentLikesResponseDtoList = likesList.stream()
                    .map(CommentLikesResponseDto :: new)
                    .toList();

            return this.resultResponse(HttpStatus.OK, "좋아요 조회", commentLikesResponseDtoList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RestApiResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    // 좋아요 추가
    public ResponseEntity<RestApiResponseDto> createLikes(Long postId, Long commentId, User user) {
        try {
            getPostId(postId);

            Optional<CommentLikes> existingLikesOptional = commentLikesRepository.findByCommentIdAndUserUserId(commentId, user.getUserId());

            if (existingLikesOptional.isPresent()) {
                CommentLikes existingLikes = existingLikesOptional.get();

                if (!existingLikes.isLikes()) {
                    existingLikes.setLikes(true);
                    existingLikes.getComment().setLikeCnt(existingLikes.getComment().getLikeCnt() + 1);
                    commentLikesRepository.save(existingLikes);
                    return this.resultResponse(HttpStatus.OK, "좋아요 클릭", new CommentLikesResponseDto(existingLikes));
                } else {
                    throw new IllegalArgumentException("이미 좋아요가 선택되어 있습니다.");
                }
            } else {
                Post post = postRepository.findById(postId)
                        .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));

                Comment comment = commentRepository.findById(commentId)
                        .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

                CommentLikes newLikes = new CommentLikes(true, comment, post, user);
                comment.setLikeCnt(comment.getLikeCnt() + 1);
                commentLikesRepository.save(newLikes);
                return this.resultResponse(HttpStatus.CREATED, "좋아요 생성", new CommentLikesResponseDto(newLikes));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RestApiResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }


    // 좋아요 취소
    public ResponseEntity<RestApiResponseDto> deleteLikes(Long postId, Long commentId, User user) {
        try {
            getPostId(postId);

            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

            CommentLikes commentLikes = commentLikesRepository.findByCommentIdAndUserUserId(commentId, user.getUserId())
                    .orElseThrow(
                            () -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

            likesValid(commentLikes, user);

            if (!commentLikes.isLikes()) {
                throw new IllegalArgumentException("이미 취소된 좋아요입니다.");
            } else {
                commentLikes.setLikes(false);
                comment.setLikeCnt(comment.getLikeCnt() - 1);
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
       postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));
    }

    // 댓글 사용자 검증
    private void commentsValid(Comment comment, User user) {
        Long writerId = comment.getUser().getUserId();
        Long loginId = user.getUserId();
        if (!writerId.equals(loginId)) {
            throw new IllegalArgumentException("작성자만 수정 혹은 삭제가 가능합니다.");
        }
    }

    // 좋아요 사용자 검증
    private void likesValid(CommentLikes commentLikes, User user) {
        Long writerId = commentLikes.getUser().getUserId();
        Long loginId = user.getUserId();
        if (!writerId.equals(loginId)) {
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }
    }
}
