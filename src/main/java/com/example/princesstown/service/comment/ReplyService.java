package com.example.princesstown.service.comment;

import com.example.princesstown.dto.comment.*;
import com.example.princesstown.entity.*;
import com.example.princesstown.repository.comment.CommentRepository;
import com.example.princesstown.repository.comment.ReplyLikesRepository;
import com.example.princesstown.repository.comment.ReplyRepository;
import com.example.princesstown.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyLikesRepository replyLikesRepository;

    // 답글 조회
    public ResponseEntity<RestApiResponseDto> getReplys(Long postId, Long commentId) {
            List<Reply> replysList = replyRepository.findAllByPostIdAndCommentIdOrderByCreatedAtAsc(postId, commentId);

            List<ReplyResponseDto> replyResponseDtoList = replysList.stream()
                    .map(ReplyResponseDto::new)
                    .toList();

            return this.resultResponse(HttpStatus.OK, "답글 조회", new PagedReplyResponseDto(replyResponseDtoList));
    }

    // 답글 생성
    public ResponseEntity<RestApiResponseDto> createReplys(Long postId, Long commentId, ReplyRequestDto requestDto, User user) {
        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));

            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

            Reply reply = new Reply(requestDto, post, comment, user);

            reply.setLikeCnt(0L);

            replyRepository.save(reply);
            return this.resultResponse(HttpStatus.CREATED, "댓글 생성", new ReplyResponseDto(reply));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RestApiResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    // 답글 수정
    public ResponseEntity<RestApiResponseDto> updateReplys(Long postId, Long commentId, Long replyId, ReplyRequestDto requestDto, User user) {
        try {
            getPostIdAndCommentId(postId, commentId);

            Reply reply = replyRepository.findById(replyId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 답글이 존재하지 않습니다."));

            replysValid(reply, user);

            reply.setContent(requestDto.getContent());

            reply.setEmoji(requestDto.getEmoji());

            replyRepository.save(reply);

            return this.resultResponse(HttpStatus.OK, "답글 수정", new ReplyResponseDto(reply));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RestApiResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    // 답글 삭제
    public ResponseEntity<RestApiResponseDto> deleteReplys(Long postId, Long commentId, Long replyId, User user) {
        try {
            getPostIdAndCommentId(postId, commentId);

            Reply reply = replyRepository.findById(replyId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 답글이 존재하지 않습니다."));

            replysValid(reply, user);

            replyRepository.delete(reply);

            return this.resultResponse(HttpStatus.OK, "답글 삭제", new ReplyResponseDto(reply));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RestApiResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    // 좋아요 가져오기
    public ResponseEntity<RestApiResponseDto> getLikes(Long postId, Long commentId) {
        try {
            getPostIdAndCommentId(postId, commentId);

            List<ReplyLikes> likesList = replyLikesRepository.findAllByCommentId(commentId);

        List<ReplyLikesResponseDto> replyLikesResponseDtoList = likesList.stream()
                .map(ReplyLikesResponseDto :: new)
                .toList();

        return this.resultResponse(HttpStatus.OK, "좋아요 조회", replyLikesResponseDtoList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RestApiResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    // 좋아요 추가
    public ResponseEntity<RestApiResponseDto> createLikes(Long postId, Long commentId, Long replyId, User user) {
        try {
            getPostIdAndCommentId(postId, commentId);

            Optional<ReplyLikes> existingLikesOptional = replyLikesRepository.findByReplyIdAndUserUserId(replyId, user.getUserId());

            if (existingLikesOptional.isPresent()) {
                ReplyLikes existingLikes = existingLikesOptional.get();

                if (!existingLikes.isLikes()) {
                    existingLikes.setLikes(true);
                    existingLikes.getReply().setLikeCnt(existingLikes.getReply().getLikeCnt() + 1);
                    replyLikesRepository.save(existingLikes);
                    return this.resultResponse(HttpStatus.CREATED, "좋아요 클릭", new ReplyLikesResponseDto(existingLikes));
                } else {
                    throw new IllegalArgumentException("이미 좋아요가 선택되어있습니다.");
                }
            } else {
                Comment comment = commentRepository.findById(commentId)
                        .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));

                Reply reply = replyRepository.findById(replyId)
                        .orElseThrow(() -> new IllegalArgumentException("해당 답글이 존재하지 않습니다."));

                ReplyLikes newLikes = new ReplyLikes(true, comment, reply, user);
                reply.setLikeCnt(reply.getLikeCnt() + 1);
                replyLikesRepository.save(newLikes);
                return this.resultResponse(HttpStatus.CREATED, "좋아요 생성", new ReplyLikesResponseDto(newLikes));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RestApiResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    // 좋아요 취소
    public ResponseEntity<RestApiResponseDto> deleteLikes(Long postId, Long commentId, Long replyId, User user) {
        try {
            getPostIdAndCommentId(postId, commentId);

            Reply reply = replyRepository.findById(replyId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 답글이 존재하지 않습니다."));

            ReplyLikes replyLikes = replyLikesRepository.findByReplyIdAndUserUserId(replyId, user.getUserId())
                    .orElseThrow(
                            () -> new IllegalArgumentException("해당 답글이 존재하지 않습니다."));

            likesValid(replyLikes, user);

            if (!replyLikes.isLikes()) {
                throw new IllegalArgumentException("이미 취소된 좋아요입니다.");
            } else {
                replyLikes.setLikes(false);
                reply.setLikeCnt(reply.getLikeCnt() - 1);
                replyLikesRepository.save(replyLikes);
            }
            return this.resultResponse(HttpStatus.OK, "좋아요 취소", new ReplyLikesResponseDto(replyLikes));
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

    // 게시물하고 댓글 가져오는 메소드 분리
    private void getPostIdAndCommentId(Long postId, Long commentId) {
        postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이나 댓글이 존재하지 않습니다."));

        commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이나 댓글이 존재하지 않습니다."));
    }

    // 답글 사용자 검증
    private void replysValid(Reply reply, User user) {
        Long writerId = reply.getUser().getUserId();
        Long loginId = user.getUserId();
        if (!writerId.equals(loginId)) {
            throw new IllegalArgumentException("작성자만 수정 혹은 삭제가 가능합니다.");
        }
    }

    // 좋아요 사용자 검증
    private void likesValid(ReplyLikes replyLikes, User user) {
        Long writerId = replyLikes.getUser().getUserId();
        Long loginId = user.getUserId();
        if (!writerId.equals(loginId)) {
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }
    }
}
