package com.example.princesstown.controller.post;

import com.example.princesstown.dto.request.PostRequestDto;
import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.dto.response.PostResponseDto;
import com.example.princesstown.entity.Post;
import com.example.princesstown.security.user.UserDetailsImpl;
import com.example.princesstown.service.post.LikeService;
import com.example.princesstown.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {
    private final PostService postService;
    private final LikeService likeService;

    //게시글 전체 조회 API
    @GetMapping("/board/posts")
    public List<PostResponseDto> getPosts(){
        return postService.getPosts();
    }

    //선택 게시판 게시글 전체 조회
    @GetMapping("/board/{boardId}/posts")
    public List<Post> getAllPostsByBoardId(@PathVariable Long boardId) {
        return postService.getAllPostsByBoardId(boardId);
    }


    //게시글 선택 조회 API
    @GetMapping("/board/{boardId}/posts/{postId}")
    public PostResponseDto getPost(@PathVariable Long boardId, @PathVariable Long postId){
        return postService.getPost(postId);
    }

    // 게시글 등록 API
    @PostMapping("/board/{boardId}/posts")
    @ResponseBody
    public ResponseEntity<ApiResponseDto> createPost(@RequestBody PostRequestDto postRequestDto,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @PathVariable Long boardId){
        log.info("title : " + postRequestDto.getTitle());
        log.info("contents : " + postRequestDto.getContents());

        postService.createPost(postRequestDto, userDetails.getUser(), boardId);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.CREATED.value(), "글 작성에 성공했습니다."));
    }
    // 게시글 수정 API
    @PutMapping("/board/{boardId}/posts/{postId}")
    public ResponseEntity<ApiResponseDto> updatePost(@PathVariable Long boardId,
                                                     @PathVariable Long postId,
                                                     @RequestBody PostRequestDto postRequestDto,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails){
        try {
            postService.updatePost(postId, postRequestDto, userDetails.getUser());
        } catch (IllegalArgumentException e) {
            return  ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "글 수정에 실패했습니다."));
        }
        return  ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "글 수정에 성공했습니다."));
    }


    // 게시글 삭제 API
    @DeleteMapping("/board/{boardId}/posts/{postId}")
    public ApiResponseDto deletePost(@PathVariable Long boardId,
                                     @PathVariable Long postId,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails){
        return postService.deletePost(postId, userDetails.getUser());
    }

    //블로그 게시글 좋아요 API
    @PostMapping("/board/{boardId}/posts/{postId}/like")
    public ResponseEntity<ApiResponseDto> likeBlog(@PathVariable Long boardId,
                                                   @PathVariable Long postId,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(likeService.likePost(postId, userDetails.getUser()));
    }

    //블로그 게시글 좋아요 취소 API
    @DeleteMapping("/board/{boardId}/posts/{postId}/like")
    public ResponseEntity<ApiResponseDto> deleteLikeBlog(@PathVariable Long boardId,
                                                         @PathVariable Long postId,
                                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(likeService.deleteLikePost(postId, userDetails.getUser()));
    }

}
