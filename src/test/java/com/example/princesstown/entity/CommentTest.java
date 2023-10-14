package com.example.princesstown.entity;

import com.example.princesstown.dto.comment.CommentRequestDto;
import com.example.princesstown.dto.comment.CommentResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentTest {

    @Test
    @DisplayName("Comment 객체 생성 테스트")
    void 댓글생성() {
        // given
        String content = "content";
        String emoji = "emoji";
        long likeCnt = 0L;
        Post post = new Post();
        User user = new User();

        // when
        CommentRequestDto requestDto = new CommentRequestDto();
        requestDto.setContent(content);
        requestDto.setEmoji(emoji);

        Comment comment = new Comment(requestDto, post, user);

        // then
        assertEquals(comment.getContent(), content);
        assertEquals(comment.getEmoji(), emoji);
        assertEquals(0L, likeCnt);
        assertEquals(comment.getPost(), post);
        assertEquals(comment.getUser(), user);
    }

}