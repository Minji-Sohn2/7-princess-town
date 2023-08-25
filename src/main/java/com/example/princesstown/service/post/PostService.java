package com.example.princesstown.service.post;

import com.example.princesstown.dto.request.PostRequestDto;
import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.dto.response.PostResponseDto;
import com.example.princesstown.entity.Board;
import com.example.princesstown.entity.Post;
import com.example.princesstown.entity.User;
import com.example.princesstown.repository.board.BoardRepository;
import com.example.princesstown.repository.post.LikeRepository;
import com.example.princesstown.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    // private final JwtUtil jwtUtil;

    // 게시글 전체 조회 API
    @Transactional(readOnly = true)
    public List<PostResponseDto> getPosts() {
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
        List<PostResponseDto> postResponseDto = new ArrayList<>();

        for(Post post : posts){
            postResponseDto.add(new PostResponseDto(post));
        }

        return postResponseDto;
    }

    // 선택 게시판 게시글 전체 조회 API
     @Transactional(readOnly = true)
    public List<Post> getAllPostsByBoardId(Long boardId) {
        return postRepository.findByBoardIdOrderByCreatedAtDesc(boardId);
    }

    // 게시글 선택 조회 API
    @Transactional(readOnly = false)
    public PostResponseDto getPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("선택하신 게시물은 존재하지 않습니다.")
        );
        return new PostResponseDto(post);

    }

    // 게시글 등록 API
    public PostResponseDto createPost(PostRequestDto postRequestDto, User user, Long boardId) {
        Board board =boardRepository.findById(boardId).orElseThrow();

        if(user == null){
            throw new IllegalArgumentException("허가되지 않은 사용자입니다.");
        }
        Post post = new Post(postRequestDto, user, board, 0L);
        postRepository.save(post);

        return new PostResponseDto(post);
    }

    // 게시글 수정 API
    @Transactional
    public PostResponseDto updatePost(Long id, PostRequestDto postRequestDto, User user) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new NullPointerException("선택하신 게시물은 존재하지 않습니다.")
        );

        if(post.getUser().getUserId().equals(user.getUserId())){ // || user.getRole().equals(UserRoleEnum.ADMIN)
            post.update(postRequestDto);
        } else {
            throw new IllegalArgumentException("작성자만 수정이 가능합니다.");
        }

        return new PostResponseDto(post);
    }


    // 게시글 삭제 API
    @Transactional
    public ApiResponseDto deletePost(Long id, User user) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new NullPointerException("선택하신 게시물은 존재하지 않습니다.")
        );
        if(post.getUser().getUserId().equals(user.getUserId())){ //  || user.getRole().equals(UserRoleEnum.ADMIN)
            postRepository.delete(post);
        } else {
            return new ApiResponseDto(400, "작성자만 삭제가 가능합니다.");
        }
        return new ApiResponseDto(200, "삭제가 완료되었습니다.");
    }

    @Transactional
    public void incrementViewCount(Long postId) {
        postRepository.incrementViewCount(postId);
    }

}
