package com.example.princesstown.service.post;

import com.example.princesstown.dto.comment.RestApiResponseDto;
import com.example.princesstown.dto.request.PostRequestDto;
import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.dto.response.PostLikesResponseDto;
import com.example.princesstown.dto.response.PostResponseDto;
import com.example.princesstown.entity.*;
import com.example.princesstown.repository.board.BoardRepository;
import com.example.princesstown.repository.post.PostLikesRepository;
import com.example.princesstown.repository.post.PostRepository;
import com.example.princesstown.repository.post.SearchHistoryRepository;
import com.example.princesstown.service.awsS3.S3Uploader;
import com.example.princesstown.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final S3Uploader s3Uploader;
    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final PostLikesRepository postLikesRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final UserService userService;


    //위치반경내 게시물 조회
    public List<PostResponseDto> getPostsAroundUser(Long userId) {
        // 1. 사용자의 위치 정보 가져오기
        Optional<Location> userLocationOpt = userService.getUserLocation(userId);

        if (!userLocationOpt.isPresent()) {
            // 사용자의 위치 정보가 없으면 빈 목록 반환
            return Collections.emptyList();
        }

        Location userLocation = userLocationOpt.get();

        // 2. 반경 내의 게시글 조회
        List<Post> allPosts = postRepository.findAllByOrderByCreatedAtDesc(); // 모든 게시글 조회(최신순)

        // 3. 반경 내의 게시글 필터링
        List<PostResponseDto> nearbyPosts = new ArrayList<>();
        for (Post post : allPosts) {
            if (isWithinRadius(userLocation, post.getLocation())) {
                // PostResponseDto를 생성하여 데이터를 복사
                PostResponseDto postResponseDto = new PostResponseDto(post);
                nearbyPosts.add(postResponseDto);
            }
        }

        return nearbyPosts;
    }

    private boolean isWithinRadius(Location userLocation, Location postLocation) {
        // 반경 내에 있는지 여부를 판단하는 로직을 구현
        double userLatitude = userLocation.getLatitude();
        double userLongitude = userLocation.getLongitude();
        double postLatitude = postLocation.getLatitude();
        double postLongitude = postLocation.getLongitude();

        double distance = calculateDistance(userLatitude, userLongitude, postLatitude, postLongitude);

        // 반경 내에 있는 게시글인지 여부 판단
        double radius = userLocation.getRadius();
        return distance <= radius;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // 좌표 간의 거리 계산 로직을 구현
        // 실제 거리 계산에는 좌표를 이용하는 라이브러리를 사용하는 것이 좋습니다.
        // 여기에서는 간단한 예시로 표시했습니다.
        double earthRadius = 6371; // 지구 반지름 (킬로미터)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

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

        saveSearchHistory(title);

        List<Post> posts = postRepository.findAllByTitleContainingIgnoreCaseOrderByCreatedAtDesc(title);
        List<PostResponseDto> postResponseDtos = new ArrayList<>();

        for (Post post : posts) {
            postResponseDtos.add(new PostResponseDto(post));
        }

        return postResponseDtos;
    }

    //게시글 내용으로 검색
    @Transactional
    public List<PostResponseDto> searchPostsByContents(String contents) {

        saveSearchHistory(contents);

        List<Post> posts = postRepository.findAllByContentsContainingIgnoreCaseOrderByCreatedAtDesc(contents);
        List<PostResponseDto> postResponseDtos = new ArrayList<>();

        for (Post post : posts) {
            postResponseDtos.add(new PostResponseDto(post));
        }

        return postResponseDtos;
    }

    //게시글 제목 + 내용으로 검색
    @Transactional
    public List<PostResponseDto> searchPostsByTitleOrContents(String keyword) {

        saveSearchHistory(keyword);

        List<Post> posts = postRepository.findAllByTitleContainingIgnoreCaseOrContentsContainingIgnoreCaseOrderByCreatedAtDesc(keyword, keyword);
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

    //게시글 조회수
    @Transactional
    public void incrementViewCount(Long postId) {
        postRepository.incrementViewCount(postId);
    }

    private void saveSearchHistory(String keyword) {
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setKeyword(keyword);
        searchHistoryRepository.save(searchHistory);
    }

    //게시글 좋아요 가져오기
    public ResponseEntity<RestApiResponseDto> getLikes(Long postId) {
        try {
            getPostId(postId);

            List<PostLikes> likesList = postLikesRepository.findAllByPostId(postId);

            List<PostLikesResponseDto> postLikesResponseDtoList = likesList.stream()
                    .map(PostLikesResponseDto::new)
                    .toList();

            return this.resultResponse(HttpStatus.OK, "게시글 좋아요 조회", postLikesResponseDtoList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RestApiResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    //게시글 좋아요
    public ResponseEntity<RestApiResponseDto> createLikes(Long postId, User user) {
        try {
            getPostId(postId);

            Optional<PostLikes> existingLikesOptional = postLikesRepository.findByPostIdAndUserUserId(postId, user.getUserId());

            if (existingLikesOptional.isPresent()) {
                PostLikes existingLikes = existingLikesOptional.get();

                if (!existingLikes.isLikes()) {
                    existingLikes.setLikes(true);
                    existingLikes.getPost().setLikeCnt(existingLikes.getPost().getLikeCnt() + 1);
                    postLikesRepository.save(existingLikes);
                    return this.resultResponse(HttpStatus.CREATED, "게시글 좋아요 클릭", new PostLikesResponseDto(existingLikes));
                } else {
                    throw new IllegalArgumentException("이미 좋아요가 선택되어 있습니다.");
                }
            } else {
                Post post = postRepository.findById(postId)
                        .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));

                PostLikes newLikes = new PostLikes(true, post, user);
                post.setLikeCnt(post.getLikeCnt() + 1);
                postLikesRepository.save(newLikes);
                return this.resultResponse(HttpStatus.CREATED, "게시글 좋아요 생성", new PostLikesResponseDto(newLikes));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RestApiResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    //게시글 좋아요 취소
    public ResponseEntity<RestApiResponseDto> deleteLikes(Long postId, User user) {
        try {
            getPost(postId);

            PostLikes postLikes = postLikesRepository.findByPostIdAndUserUserId(postId, user.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 게시글 좋아요가 존재하지 않습니다."));

            likesValid(postLikes, user);

            if (!postLikes.isLikes()) {
                throw new IllegalArgumentException("이미 취소된 게시글 좋아요입니다.");
            } else {
                postLikes.setLikes(false);
                postLikes.getPost().setLikeCnt(postLikes.getPost().getLikeCnt() - 1);
                postLikesRepository.save(postLikes);
            }
            return this.resultResponse(HttpStatus.OK, "게시글 좋아요 취소", new PostLikesResponseDto(postLikes));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RestApiResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }


    // 상태코드, 메세지, json 반환
    private ResponseEntity<RestApiResponseDto> resultResponse(HttpStatus status, String message, Object result) {
        RestApiResponseDto restApiResponseDto = new RestApiResponseDto(status.value(), message, result);
        return new ResponseEntity<>(
                restApiResponseDto,
                status
        );
    }

    // 좋아요 사용자 검증
    private void likesValid(PostLikes postLikes, User user) {
        Long writerId = postLikes.getUser().getUserId();
        Long loginId = user.getUserId();
        if (!writerId.equals(loginId)) {
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }
    }

    // 게시물Id 가져오는 메소드 분리
    private void getPostId(Long postId) {
        postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));
    }

}
