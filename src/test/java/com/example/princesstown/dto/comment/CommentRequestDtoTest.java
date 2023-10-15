package com.example.princesstown.dto.comment;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentRequestDtoTest {
    @Test
    @DisplayName("CommentRequestDto 테스트")
    void 요청테스트() {
        //given
        String content = "content";
        String emoji = "/img/emoji/emoji1.png";

        //when
        CommentRequestDto requestDto = new CommentRequestDto();
        requestDto.setContent(content);
        requestDto.setEmoji(emoji);

        //then
        assertThat(requestDto.getContent()).isEqualTo(content);
        assertThat(requestDto.getEmoji()).isEqualTo(emoji);

        System.out.println("requestDto.getContent() = " + requestDto.getContent());
        System.out.println("requestDto.getEmoji() = " + requestDto.getEmoji());
    }
}