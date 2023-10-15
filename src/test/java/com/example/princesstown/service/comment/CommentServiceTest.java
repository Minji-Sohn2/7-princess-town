package com.example.princesstown.service.comment;

import com.example.princesstown.dto.comment.CommentRequestDto;
import com.example.princesstown.dto.comment.CommentResponseDto;
import com.example.princesstown.entity.Comment;
import com.example.princesstown.entity.Post;
import com.example.princesstown.entity.User;
import com.example.princesstown.repository.comment.CommentRepository;
import com.example.princesstown.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Nested
    class 댓글생성 {
        @Test
        @Transactional
        @DisplayName("댓글 생성 성공테스트")
        void 댓글생성성공테스트() {
            // given
            CommentRequestDto requestDto = new CommentRequestDto();
            Post post = new Post();

            User user = userRepository.findByUsername("ANz63hS1Q_gX48lDe3nHjQm3Po5xMVaogpJpItWCJbk_NaverUser_f24b8a11-5af6-44bb-92f4-c85c287c3265")
                            .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

            post.setId(1L);

            requestDto.setContent("댓글생성");
            requestDto.setEmoji("/img/emoji/emoji1.png");

            // when
            Comment comment = new Comment(requestDto, post, user);

            commentService.createComments(post.getId(), requestDto, user);

            CommentResponseDto responseDto = new CommentResponseDto(comment);

            // then
            assertThat(responseDto.getContent()).isEqualTo("댓글생성");
            assertThat(responseDto.getEmoji()).isEqualTo("/img/emoji/emoji1.png");
        }

        @Test
        @Transactional
        @DisplayName("댓글 생성 실패테스트")
        void 댓글생성실패테스트() {
            // given
            CommentRequestDto requestDto = new CommentRequestDto();
            Post post = new Post();
            User user = userRepository.findByUsername("ANz63hS1Q_gX48lDe3nHjQm3Po5xMVaogpJpItWCJbk_NaverUser_f24b8a11-5af6-44bb-92f4-c85c287c3265")
                    .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

            post.setId(1L);

            requestDto.setContent("");
            requestDto.setEmoji("");

            // when
            commentService.createComments(post.getId(), requestDto, user);

            Comment comment = new Comment(requestDto, post, user);

            CommentResponseDto responseDto = new CommentResponseDto(comment);

            // then

            assertThat(responseDto.getContent()).isBlank();
            assertThat(responseDto.getEmoji()).isBlank();
        }
    }

    @Nested
    @DisplayName("댓글 수정 테스트")
    class 댓글수정테스트 {

        @BeforeEach
        @Transactional
        void 댓글생성() {
            CommentRequestDto requestDto = new CommentRequestDto();
            Post post = new Post();
            User user = userRepository.findByUsername("ANz63hS1Q_gX48lDe3nHjQm3Po5xMVaogpJpItWCJbk_NaverUser_f24b8a11-5af6-44bb-92f4-c85c287c3265")
                    .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

            post.setId(1L);

            requestDto.setContent("댓글생성");
            requestDto.setEmoji("/img/emoji/emoji1.png");

            // when
            Comment comment = new Comment(requestDto, post, user);

            commentService.createComments(post.getId(), requestDto, user);

            System.out.println("comment.getContent() = " + comment.getContent());
            System.out.println("comment.getEmoji() = " + comment.getEmoji());
        }

        @Test
        @Transactional
        @DisplayName("댓글 수정 성공테스트")
        void 댓글수정성공테스트() {

            // given
            CommentRequestDto requestDto = new CommentRequestDto();
            Post post = new Post();
            User user = userRepository.findByUsername("ANz63hS1Q_gX48lDe3nHjQm3Po5xMVaogpJpItWCJbk_NaverUser_f24b8a11-5af6-44bb-92f4-c85c287c3265")
                    .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

            post.setId(1L);

            requestDto.setContent("댓글수정");
            requestDto.setEmoji("/img/emoji/emoji2.png");

            // when
            commentService.updateComments(post.getId(), 1L, requestDto, user);

            Comment comment = new Comment(requestDto, post, user);

            CommentResponseDto responseDto = new CommentResponseDto(comment);

            // then
            assertThat(responseDto.getContent()).isEqualTo("댓글수정");
            assertThat(responseDto.getEmoji()).isEqualTo("/img/emoji/emoji2.png");

            System.out.println("comment.getContent() = " + comment.getContent());
            System.out.println("comment.getEmoji() = " + comment.getEmoji());
        }

        @Test
        @Transactional
        @DisplayName("댓글 수정 실패테스트")
        void 댓글수정실패테스트() {
            // given
            CommentRequestDto requestDto = new CommentRequestDto();
            Post post = new Post();
            User user = userRepository.findByUsername("ANz63hS1Q_gX48lDe3nHjQm3Po5xMVaogpJpItWCJbk_NaverUser_f24b8a11-5af6-44bb-92f4-c85c287c3265")
                    .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

            post.setId(1L);

            requestDto.setContent("");
            requestDto.setEmoji("");

            // when
            commentService.updateComments(post.getId(), 1L, requestDto, user);

            Comment comment = new Comment(requestDto, post, user);

            CommentResponseDto responseDto = new CommentResponseDto(comment);

            // then
            assertThat(responseDto.getContent()).isBlank();
            assertThat(responseDto.getEmoji()).isBlank();

            System.out.println("responseDto.getContent() = " + responseDto.getContent());
            System.out.println("responseDto.getEmoji() = " + responseDto.getEmoji());
        }
    }

    @Nested
    @DisplayName("댓글 삭제 테스트")
    class 댓글삭제테스트 {
        @BeforeEach
        @Transactional
        void 댓글생성() {
            CommentRequestDto requestDto = new CommentRequestDto();
            Post post = new Post();
            User user = userRepository.findByUsername("ANz63hS1Q_gX48lDe3nHjQm3Po5xMVaogpJpItWCJbk_NaverUser_f24b8a11-5af6-44bb-92f4-c85c287c3265")
                    .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

            post.setId(10L);

            requestDto.setContent("댓글생성");
            requestDto.setEmoji("/img/emoji/emoji1.png");

            // when
            Comment comment = new Comment(requestDto, post, user);

            comment.setId(111L);

            commentService.createComments(post.getId(), requestDto, user);

            System.out.println("comment.getContent() = " + comment.getContent());
            System.out.println("comment.getEmoji() = " + comment.getEmoji());

            System.out.println("commentRepository.findById(111L) = " + commentRepository.findById(111L));
        }

        @Test
        @Transactional
        @DisplayName("댓글 삭제 성공테스트")
        void 댓글삭제성공테스트() {
            // given
            CommentRequestDto requestDto = new CommentRequestDto();
            Post post = new Post();
            User user = userRepository.findByUsername("ANz63hS1Q_gX48lDe3nHjQm3Po5xMVaogpJpItWCJbk_NaverUser_f24b8a11-5af6-44bb-92f4-c85c287c3265")
                    .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

            post.setId(10L);

            // when
            commentService.deleteComments(post.getId(), 111L, user);

            // then
            assertThat(commentRepository.findById(111L)).isEmpty();

            System.out.println("commentRepository.findById(111L) = " + commentRepository.findById(111L));
        }

        @Test
        @Transactional
        @DisplayName("댓글 삭제 실패테스트")
        void 댓글삭제실패테스트() {
            // given
            CommentRequestDto requestDto = new CommentRequestDto();
            Post post = new Post();
            User user = userRepository.findByUsername("ANz63hS1Q_gX48lDe3nHjQm3Po5xMVaogpJpItWCJbk_NaverUser_f24b8a11-5af6-44bb-92f4-c85c287c3265")
                    .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

            post.setId(10L);

            // when
            commentService.deleteComments(post.getId(), 111L, user);

            // then
            assertThat(commentRepository.findById(111L)).isNotEmpty();

            System.out.println("commentRepository.findById(111L) = " + commentRepository.findById(115L));
        }

    }

}