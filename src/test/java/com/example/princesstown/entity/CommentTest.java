package com.example.princesstown.entity;

import com.example.princesstown.dto.comment.CommentRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CommentTest {

    @Test
    @DisplayName("Comment 객체 생성 테스트")
    void 댓글생성() {
        // given
        String content = "content";
        String emoji = "emoji";
        Post post = new Post();
        User user = new User();

        // when
        CommentRequestDto requestDto = new CommentRequestDto();
        requestDto.setContent(content);
        requestDto.setEmoji(emoji);

        Comment comment = new Comment(requestDto, post, user);

        // then

        assertThat(comment.getContent()).isEqualTo(content);
        assertThat(comment.getEmoji()).isEqualTo(emoji);
        assertThat(comment.getPost()).isSameAs(post);
        assertThat(comment.getUser()).isSameAs(user);

        System.out.println("comment.getContent() = " + comment.getContent());
        System.out.println(content);
    }

}