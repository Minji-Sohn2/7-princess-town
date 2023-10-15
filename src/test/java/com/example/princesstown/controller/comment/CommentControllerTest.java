package com.example.princesstown.controller.comment;

import com.example.princesstown.dto.comment.CommentRequestDto;
import com.example.princesstown.entity.Comment;
import com.example.princesstown.entity.CommentLikes;
import com.example.princesstown.entity.User;
import com.example.princesstown.repository.comment.CommentLikesRepository;
import com.example.princesstown.repository.comment.CommentRepository;
import com.example.princesstown.repository.user.UserRepository;
import com.example.princesstown.security.user.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    MockMvc mockMvc;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentLikesRepository commentLikesRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    @WithMockUser
    @Transactional
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @DisplayName("댓글 조회 테스트")
    public void 댓글조회() throws Exception {
        this.mockMvc
                .perform(get("/api/posts/{postId}/comments", 10L))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 조회 실패")
    public void 댓글조회실패() throws Exception {
        this.mockMvc
                .perform(get("/api/posts/comments"))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    @Transactional
    @DisplayName("댓글 작성 테스트")
//    @WithMockUser(username = "testUser", roles = "USER")
    public void 댓글작성() throws Exception {
        CommentRequestDto requestDto = new CommentRequestDto();
        requestDto.setContent("댓글작성");
        requestDto.setEmoji("/img/emoji/emoji1.png");
        User user = userRepository.findByUsername("ANz63hS1Q_gX48lDe3nHjQm3Po5xMVaogpJpItWCJbk_NaverUser_f24b8a11-5af6-44bb-92f4-c85c287c3265")
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        this.mockMvc
                .perform(post("/api/posts/{postId}/comments", 1L)
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @Transactional
    @DisplayName("댓글 작성 실패")
    public void 댓글작성실패() throws Exception {
        CommentRequestDto requestDto = new CommentRequestDto();
        requestDto.setContent("댓글작성");
        requestDto.setEmoji("/img/emoji/emoji1.png");
        User user = userRepository.findByUsername("ANz63hS1Q_gX48lDe3nHjQm3Po5xMVaogpJpItWCJbk_NaverUser_f24b8a11-5af6-44bb-92f4-c85c287c3265")
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        this.mockMvc
                .perform(post("/api/posts/{postId}/comments", 100L)
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    @Transactional
    @DisplayName("댓글 수정 테스트")
//    @WithMockUser
    public void 댓글수정() throws Exception {
        CommentRequestDto requestDto = new CommentRequestDto();
        requestDto.setContent("댓글수정");
        requestDto.setEmoji("/img/emoji/emoji1.png");

        User user = userRepository.findByUsername("ANz63hS1Q_gX48lDe3nHjQm3Po5xMVaogpJpItWCJbk_NaverUser_f24b8a11-5af6-44bb-92f4-c85c287c3265")
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        this.mockMvc
                .perform(put("/api/posts/{postId}/comments/{commentId}", 1L, 3L)
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Transactional
    @DisplayName("댓글 수정 실패")
    public void 댓글수정실패() throws Exception {
        CommentRequestDto requestDto = new CommentRequestDto();
        requestDto.setContent("댓글수정");
        requestDto.setEmoji("/img/emoji/emoji2.png");
        User user = userRepository.findByUsername("ANz63hS1Q_gX48lDe3nHjQm3Po5xMVaogpJpItWCJbk_NaverUser_f24b8a11-5af6-44bb-92f4-c85c287c3265")
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        this.mockMvc
                .perform(put("/api/posts/{postId}/comments/{commentId}", 100L, 300L)
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    @Transactional
    @DisplayName("댓글 삭제 테스트")
//    @WithMockUser
    public void 댓글삭제() throws Exception {
        Comment comment = new Comment();
        comment.setContent("댓글삭제");
        comment.setEmoji("/img/emoji/emoji1.png");

        commentRepository.save(comment);

        User user = userRepository.findByUsername("ANz63hS1Q_gX48lDe3nHjQm3Po5xMVaogpJpItWCJbk_NaverUser_f24b8a11-5af6-44bb-92f4-c85c287c3265")
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        this.mockMvc
                .perform(delete("/api/posts/{postId}/comments/{commentId}", 1L, 3L)
                        .with(user(userDetails))
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Transactional
    @DisplayName("댓글 삭제 실패")
    public void 댓글삭제실패() throws Exception {
        User user = userRepository.findByUsername("ANz63hS1Q_gX48lDe3nHjQm3Po5xMVaogpJpItWCJbk_NaverUser_f24b8a11-5af6-44bb-92f4-c85c287c3265")
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        this.mockMvc
                .perform(delete("/api/posts/{postId}/comments/{commentId}", 100L, 300L)
                        .with(user(userDetails))
                )
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    @Transactional
    @DisplayName("좋아요 조회 테스트")
    public void 좋아요조회() throws Exception {
        this.mockMvc
                .perform(get("/api/posts/{postId}/comments/likes", 1L))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Transactional
    @DisplayName("좋아요 추가 테스트")
    public void 좋아요추가() throws Exception {

        User user = userRepository.findByUsername("ANz63hS1Q_gX48lDe3nHjQm3Po5xMVaogpJpItWCJbk_NaverUser_f24b8a11-5af6-44bb-92f4-c85c287c3265")
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        this.mockMvc
                .perform(post("/api/posts/{postId}/comments/{commentId}/likes", 1L, 1L)
                        .with(user(userDetails))
                )
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    @Transactional
    @DisplayName("좋아요 실패 테스트")
    public void 좋아요실패() throws Exception {

        User user = userRepository.findByUsername("ANz63hS1Q_gX48lDe3nHjQm3Po5xMVaogpJpItWCJbk_NaverUser_f24b8a11-5af6-44bb-92f4-c85c287c3265")
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        this.mockMvc
                .perform(post("/api/posts/{postId}/comments/{commentId}/likes", 100L, 100L)
                        .with(user(userDetails))
                )
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    @Transactional
    @DisplayName("좋아요 취소 테스트")
    public void 좋아요취소() throws Exception {
        User user = userRepository.findByUsername("ANz63hS1Q_gX48lDe3nHjQm3Po5xMVaogpJpItWCJbk_NaverUser_f24b8a11-5af6-44bb-92f4-c85c287c3265")
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        CommentLikes commentLikes = commentLikesRepository.findByCommentIdAndUserUserId(1L, user.getUserId())
                .orElseThrow(
                        () -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
        commentLikes.setLikes(true);
        commentLikesRepository.save(commentLikes);

        this.mockMvc
                .perform(put("/api/posts/{postId}/comments/{commentId}/likes", 1L, 1L)
                        .with(user(userDetails))
                )
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    @Transactional
    @DisplayName("좋아요 취소 실패 테스트")
    public void 좋아요취소실패() throws Exception {

        User user = userRepository.findByUsername("ANz63hS1Q_gX48lDe3nHjQm3Po5xMVaogpJpItWCJbk_NaverUser_f24b8a11-5af6-44bb-92f4-c85c287c3265")
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        this.mockMvc
                .perform(put("/api/posts/{postId}/comments/{commentId}/likes", 100L, 100L)
                        .with(user(userDetails))
                )
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }
}