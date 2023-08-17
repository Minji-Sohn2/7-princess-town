package com.example.princesstown.service.comment;

import com.example.princesstown.dto.comment.CommentRequestDto;
import com.example.princesstown.dto.comment.RestApiResponseDto;
import com.example.princesstown.repository.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public ResponseEntity<RestApiResponseDto> getComments(Long postId, CommentRequestDto requestDto) {
        try {

        } catch (IllegalArgumentException e) {

        }
        return null;
    }

    public ResponseEntity<RestApiResponseDto> createComments(Long postId, CommentRequestDto requestDto, userDetails) {
        try {

        } catch (IllegalArgumentException e) {

        }
        return null;
    }

    public ResponseEntity<RestApiResponseDto> updateComments(Long postId, CommentRequestDto requestDto, userDetails) {
        try {

        } catch (IllegalArgumentException e) {

        }
        return null;
    }

    public ResponseEntity<RestApiResponseDto> deleteComments(Long postId, CommentRequestDto requestDto, userDetails) {
        try {

        } catch (IllegalArgumentException e) {

        }
        return null;
    }

    public ResponseEntity<RestApiResponseDto> liketogle(Long postId, CommentRequestDto requestDto, userDetails) {
        try {

        } catch (IllegalArgumentException e) {

        }
        return null;
    }
}
