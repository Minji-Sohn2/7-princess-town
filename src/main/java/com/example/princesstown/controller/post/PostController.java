package com.example.princesstown.controller.post;

import com.example.princesstown.dto.comment.RestApiResponseDto;
import com.example.princesstown.dto.request.PostRequestDto;
import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.dto.response.PostResponseDto;
import com.example.princesstown.entity.Post;
import com.example.princesstown.security.user.UserDetailsImpl;
import com.example.princesstown.service.awsS3.S3Uploader;
import com.example.princesstown.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

    private final PostService postService;
    private final S3Uploader s3Uploader;

    // 선택 게시판 내 위치반경 내 게시글 조회 API
    @GetMapping("/boards/{boardId}/radius/posts")
    public ResponseEntity<List<PostResponseDto>> getPostsAroundUserByBoardId(
            @PathVariable Long boardId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getUser().getUserId();
        List<PostResponseDto> nearbyPostsInBoard = postService.getPostsAroundUserByBoardId(boardId, userId);

        if (nearbyPostsInBoard.isEmpty()) {
            // 주변의 게시글이 없을 경우 적절한 응답을 반환합니다.
            return ResponseEntity.noContent().build();
        }

        // 주변의 게시글을 반환합니다.
        return ResponseEntity.ok(nearbyPostsInBoard);
    }

    //위치반경 내 게시물 조회
    @GetMapping("/radius/posts")
    public ResponseEntity<List<PostResponseDto>> getPostsAroundUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getUserId();
        List<PostResponseDto> nearbyPosts = postService.getPostsAroundUser(userId);

         if (nearbyPosts.isEmpty()) {
            // 주변의 게시글이 없을 경우 적절한 응답을 반환합니다.
            return ResponseEntity.noContent().build();
        }

        // 주변의 게시글을 반환합니다.
        return ResponseEntity.ok(nearbyPosts);
    }

    //게시글 전체 조회 API
    @GetMapping("/boards/posts")
    public List<PostResponseDto> getPosts(){
        return postService.getPosts();
    }

    //선택 게시판 게시글 전체 조회
    @GetMapping("/boards/{boardId}/posts")
    public List<Post> getAllPostsByBoardId(@PathVariable Long boardId) {
        return postService.getAllPostsByBoardId(boardId);
    }

    //게시글 선택 조회 API
    @GetMapping("/posts/{postId}")
    public PostResponseDto getPost(@PathVariable Long postId){

        postService.incrementViewCount(postId);

        return postService.getPost(postId);
    }

    //게시글 제목 또는 내용으로 검색
    @GetMapping("/search")
    public List<PostResponseDto> searchPostsByTitleOrContents(@RequestParam String keyword) {
        return postService.searchPostsByTitleOrContents(keyword);
    }

    //게시글 내용으로 검색
    @GetMapping("/search/contents")
    public List<PostResponseDto> searchPostsByContents(@RequestParam String contents) {
        return postService.searchPostsByContents(contents);
    }

    //게시글 제목으로 검색
    @GetMapping("/search/title")
    public List<PostResponseDto> searchPostsByTitle(@RequestParam String title) {
        return postService.searchPostsByTitle(title);
    }

    // 인기검색어 top10
    @GetMapping("/top")
    public ResponseEntity<List<PostResponseDto>> getTop10LikedPostsWithDuplicates() {
        List<PostResponseDto> topPosts = postService.getTop10LikedPostsWithDuplicates();
        return ResponseEntity.ok(topPosts);
    }

    // 게시글 등록 API
    @PostMapping("/boards/{boardId}/posts")
    @ResponseBody
    public ResponseEntity<ApiResponseDto> createPost(@ModelAttribute PostRequestDto postRequestDto,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @PathVariable Long boardId,
                                                     @RequestPart(value = "postImage", required = false) MultipartFile postImage){

        log.info("title : " + postRequestDto.getTitle());
        log.info("contents : " + postRequestDto.getContents());

        if (postImage != null && !postImage.isEmpty()) {
            try {
                String imageUrl = s3Uploader.upload(postImage, "post-images");
                postRequestDto.setPostImageUrl(imageUrl);
                log.info("image : " + imageUrl);
            } catch (IOException e) {
                log.error("이미지 업로드 실패: " + e.getMessage());
                return ResponseEntity.badRequest().body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "이미지 업로드에 실패했습니다."));
            }
        }


        postService.createPost(postRequestDto, userDetails.getUser(), boardId, postImage);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.CREATED.value(), "글 작성에 성공했습니다."));

    }

    // 게시글 수정 API
    @PutMapping("/boards/{boardId}/posts/{postId}")
    public ResponseEntity<ApiResponseDto> updatePost(@PathVariable Long boardId,
                                                     @PathVariable Long postId,
                                                     @ModelAttribute PostRequestDto postRequestDto,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            postService.updatePost(boardId, postId, postRequestDto, userDetails.getUser());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "글 수정에 실패했습니다."));
        }
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "글 수정에 성공했습니다."));
    }


    // 게시글 삭제 API
    @DeleteMapping("/posts/{postId}")
    public ApiResponseDto deletePost(@PathVariable Long postId,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails){
        return postService.deletePost(postId, userDetails.getUser());
    }

    // 게시글 좋아요 조회
    @GetMapping("/posts/{postId}/likes")
    public ResponseEntity<RestApiResponseDto> getPostLikes(
            @PathVariable Long postId
    ) {
        return postService.getLikes(postId);
    }

    // 게시물 좋아요 추가
    @PostMapping("/posts/{postId}/likes")
    public ResponseEntity<RestApiResponseDto> createPostLikes(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return postService.createLikes(postId, userDetails.getUser());
    }

    // 게시물 좋아요 취소
    @DeleteMapping("/posts/{postId}/likes")
    public ResponseEntity<RestApiResponseDto> deletePostLikes(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return postService.deleteLikes(postId, userDetails.getUser());
    }

}
