package com.example.princesstown.security.post;

import com.example.princesstown.entity.Post;
import com.example.princesstown.repository.post.PostRepository;
import com.example.princesstown.security.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.RejectedExecutionException;

@Slf4j(topic = "게시글 Service")
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostLikedInfoRepository postLikedInfoRepository;

    public PostResponseDto createPost(PostRequestDto requestDto, User user) {

        // RequestDto -> Entity ( 받아온 rqeustDto를 entity에 저장하여 새로운 Post만들기)
        Post post = new Post(requestDto, user);

        // DB 저장 (새로운 Post를 DB에 저장하고 그 Post의 참조변수 savePost를 따로 정의)
        Post savePost = postRepository.save(post);

        // Entity -> ResponseDto( 저장된 post를 responseBody로 클라이언트에 반환하게 만드는 과정)
        PostResponseDto postResponseDto = new PostResponseDto(savePost);

        return postResponseDto;
    }


    // 전체 게시글 조회
    public List<PostResponseDto> getPosts() {
        return postRepository.findAllByOrderByModifiedAtDesc().stream().map(PostResponseDto::new).toList();
    } // 게시물을 가져와서 최신 수정일자 기준으로 내림차순으로 정렬한 후, 각 게시물을 리스트로 만들기 위해 맵핑함.


    public PostResponseDto lookupPost(Long id) {
        Post post = findPost(id); // 해당 id의 Post객체를 참조하는 필드 post 선언하기
        return new PostResponseDto(post); // 해당 id의 Post객체를 PostResponseDto로 넣어 컨트롤러로 반환
    }

    public Post findPost(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("선택한 게시물은 존재하지 않습니다."));
    } // 해당 id의 Post객체 반환


    @Transactional
    public ResponseEntity<ApiResponseDto> updatePost(Long id, PostRequestDto postRequestDto, User user) {
        // ResponseEntity :  HTTP 응답의 상태 코드, 헤더, 본문 등을 포함시킬 수 있는 클래스
        // ApiResponseDto로 객체를 저장하여 status, message, data를 하나의 객체로 만듬

        Optional<Post> post = postRepository.findById(id); // 해당id의 Post 가져오기

        if (!post.isPresent() || !Objects.equals(post.get().getUser().getUsername(), user.getUsername())
                &&!user.getRole().equals(UserRoleEnum.ADMIN)) {
            log.error("게시글이 존재하지 않거나 수정 권한이 없습니다.");
            return ResponseEntity.status(400).body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "게시글 수정 실패"));
        }



        post.get().updatePost(postRequestDto); // Optional객체에서 값이 존재하면 update에 postRequestDto를 전달하면서 Post를 업데이트함, 없으면 예외처리 발생시킴)

        return ResponseEntity.status(200).body(new ApiResponseDto(HttpStatus.OK.value(), "게시글 수정 성공",new PostResponseDto(post.get())));
    }



    @Transactional
    public ResponseEntity<ApiResponseDto> deletePost(Long id, User user) {
        Optional<Post> post = postRepository.findById(id);

        if(!post.isPresent() || !Objects.equals(post.get().getUser().getUsername(),user.getUsername())
                &&!user.getRole().equals(UserRoleEnum.ADMIN)) {
            log.error("게시글이 존재하지 않거나 수정 권한이 없습니다.");
            return ResponseEntity.status(400).body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "게시글 삭제 실패"));
        }

        postRepository.delete(post.get());

        return ResponseEntity.status(200).body(new ApiResponseDto(HttpStatus.OK.value(), "게시글 삭제 성공"));
    }
    // post 좋아요
    @Transactional
    public ApiResponseDto addLikePost(Long postId, UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        // postId와 userId 를이용해서 사용자가 이미 Like를 눌렀는지 확인

        // 해당 게시물이 존재하는지 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 게시글이 존재하지 않습니다."));

        //자신의 게시글에 좋아요 X
        if (post.getUser().getId().equals(userId)) {
            throw new RejectedExecutionException("자신의 게시글에는 '좋아요'를 할 수 없습니다.");
        }
        PostLikedInfo postLikedInfo = postLikedInfoRepository.findByPostIdAndUserId(postId, userId).orElse(null);

        if (postLikedInfo == null) {
            postLikedInfo = new PostLikedInfo(postId, userId);
            postLikedInfo.setLiked(true);
            postLikedInfoRepository.save(postLikedInfo);
            updatePostLikedCount(postId);
            return new ApiResponseDto(200, "좋아요");
        } else {
            postLikedInfo.setLiked(!postLikedInfo.getLiked());
            postLikedInfoRepository.save(postLikedInfo);
            updatePostLikedCount(postId);
            if (postLikedInfo.getLiked()) {
                return new ApiResponseDto(200, "좋아요");
            } else {
                return new ApiResponseDto(200, "좋아요 취소");
            }
        }
    }

    // count한 like 저장해주기
    private void updatePostLikedCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        Integer postLikedCount = postLikedInfoRepository.countByPostIdAndLikedIsTrue(postId);
        post.setPostLikedCount(postLikedCount);
    }
}
