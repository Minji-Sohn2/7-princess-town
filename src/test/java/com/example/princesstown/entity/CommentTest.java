package com.example.princesstown.entity;

import com.example.princesstown.dto.comment.CommentRequestDto;
import com.example.princesstown.repository.comment.CommentRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CommentTest {

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @Transactional
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

        final long saveId = commentRepository.save(comment).getId();

        System.out.println("saveId = " + saveId);

        // then

        final Comment commentResult = commentRepository.findById(saveId).get();

        assertThat(commentResult.getContent()).isEqualTo(content);
        assertThat(commentResult.getEmoji()).isEqualTo(emoji);
        assertThat(commentResult.getPost()).isSameAs(post);
        assertThat(commentResult.getUser()).isSameAs(user);

        System.out.println("comment.getContent() = " + commentResult.getContent());
        System.out.println(commentResult);
    }

}