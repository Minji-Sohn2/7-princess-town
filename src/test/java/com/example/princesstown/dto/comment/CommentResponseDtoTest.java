package com.example.princesstown.dto.comment;

import com.example.princesstown.entity.Comment;
import com.example.princesstown.entity.Post;
import com.example.princesstown.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CommentResponseDtoTest {

    @Nested
    @DisplayName("CommentResponseDto 테스트")
    class 기능테스트 {
        @Test
        @DisplayName("Dto에 데이터가 담겼을시")
        public void 성공테스트() {
            String content = "content";
            String emoji = "emoji";

            CommentRequestDto requestDto = new CommentRequestDto();

            Post post = new Post();

            User user = new User();

            requestDto.setContent(content);
            requestDto.setEmoji(emoji);

            Comment comment = new Comment(requestDto, post, user);

            CommentResponseDto responseDto = new CommentResponseDto(comment);

            assertThat(responseDto.getContent()).isEqualTo(content);
            assertThat(responseDto.getEmoji()).isEqualTo(emoji);
        }

        @Test
        @DisplayName("Dto에 데이터가 담기지않을시")
        public void 실패테스트() {
            String content = "content";
            String emoji = "emoji";

            CommentRequestDto requestDto = new CommentRequestDto();

            Post post = new Post();

            User user = new User();

            Comment comment = new Comment(requestDto, post, user);

            CommentResponseDto responseDto = new CommentResponseDto(comment);

            assertThat(responseDto.getContent()).isNull();
            assertThat(responseDto.getEmoji()).isNull();
        }

    }
}