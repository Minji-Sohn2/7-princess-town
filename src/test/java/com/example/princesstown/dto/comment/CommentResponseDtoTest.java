package com.example.princesstown.dto.comment;

import com.example.princesstown.entity.Comment;
import com.example.princesstown.entity.Post;
import com.example.princesstown.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentResponseDtoTest {

    @Test
    void 기능테스트() {
        String content = "content";
        String emoji = "emoji";

        CommentRequestDto requestDto = new CommentRequestDto();

        Post post = new Post();

        User user = new User();

        requestDto.setContent(content);
        requestDto.setEmoji(emoji);

        Comment comment = new Comment(requestDto, post, user);

        CommentResponseDto responseDto = new CommentResponseDto(comment);

        Assertions.assertThat(responseDto.getContent()).isEqualTo(content);
        Assertions.assertThat(responseDto.getEmoji()).isEqualTo(emoji);
    }
}