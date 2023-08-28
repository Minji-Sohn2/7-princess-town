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
import com.example.princesstown.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final S3Uploader s3Uploader;
    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
//    private final JwtUtil jwtUtil;

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

    //게시글 제목으로 검색
    public List<PostResponseDto> searchPostsByTitle(String title) {
        List<Post> posts = postRepository.findAllByTitleContainingIgnoreCaseOrderByCreatedAtDesc(title);
        List<PostResponseDto> postResponseDtos = new ArrayList<>();

        for (Post post : posts) {
            postResponseDtos.add(new PostResponseDto(post));
        }

        return postResponseDtos;
    }

    // 인기 검색어 top10
    public List<PostResponseDto> getTop10LikedPostsWithDuplicates() {
        List<Post> top10Posts = postRepository.findTop10LikedPostsWithDuplicates();
        List<PostResponseDto> postResponseDto = new ArrayList<>();

        int count = 0;
        for (Post post : top10Posts) {
            if (count >= 10) {
                break;
            }
            postResponseDto.add(new PostResponseDto(post));
            count++;
        }

        return postResponseDto;
    }

    // 게시글 등록 API
    public PostResponseDto createPost(PostRequestDto postRequestDto, User user, Long boardId, MultipartFile postImage) {
        Board board =boardRepository.findById(boardId).orElseThrow();

        if(user == null){
            throw new IllegalArgumentException("허가되지 않은 사용자입니다.");
        }

        String postImageUrl = null;

        try {
            if (postImage != null && !postImage.isEmpty()) {
                // S3에 이미지를 업로드하고, 이미지 URL을 받아옴
                postImageUrl = s3Uploader.upload(postImage, "post-images");
                log.info("업로드 성공");
            }
        } catch (IOException e) {
            // 로깅 또는 적절한 에러 처리
            log.info("업로드 실패");
            throw new IllegalArgumentException("이미지 업로드 중 오류가 발생했습니다.");

        }

        Post post = new Post(postRequestDto, user, board, 0L, postImageUrl);
        postRepository.save(post);

        return new PostResponseDto(post);


    }

    // 게시글 수정 API
    @Transactional
    public PostResponseDto updatePost(Long id, PostRequestDto postRequestDto, User user) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new NullPointerException("선택하신 게시물은 존재하지 않습니다.")
        );

        if (post.getUser().getUserId().equals(user.getUserId())) {
            post.update(postRequestDto);

            MultipartFile newPostImage = postRequestDto.getNewPostImage();
            if (newPostImage != null && !newPostImage.isEmpty()) {
                try {
                    String imageUrl = s3Uploader.upload(newPostImage, "post-images");
                    post.setImageUrl(imageUrl); // setImageUrl 메서드를 사용하여 이미지 URL 업데이트
                } catch (IOException e) {
                    log.info("업로드 실패");
                    throw new IllegalArgumentException("이미지 업로드 중 오류가 발생했습니다.");
                }
            } else if (postRequestDto.getPostImage() == null && postRequestDto.getPostImageUrl() == null) {
                // 이미지를 변경하지 않을 경우 이미지 관련 내용을 초기화
                post.setImageUrl(null);
            }

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
