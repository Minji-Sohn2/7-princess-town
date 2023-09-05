//package com.example.princesstown.service.post;
//
//
//import com.example.princesstown.dto.response.ApiResponseDto;
//import com.example.princesstown.entity.PostLikes;
//import com.example.princesstown.entity.Post;
//import com.example.princesstown.entity.User;
//import com.example.princesstown.repository.post.PostLikesRepository;
//import com.example.princesstown.repository.post.PostRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class LikeService {
//    private final PostLikesRepository postLikesRepository;
//    private final PostRepository postRepository;
////    private final CommentRepository commentRepository;
//
//
////    @Transactional
////    public ApiResponseDto likePost(Long id, User user) {
////
////        //해당 게시글이 존재하는지 확인
////        Post post = postRepository.findById(id).orElseThrow(() -> new NullPointerException("Could Not found post"));
////
////        //해당 게시글에 좋아요를 누른 아이디인지 체크
////        Like checkLike = likeRepository.findByUserUserIdAndPostId(user.getUserId(), id);
////        if (checkLike != null) {
////
////            return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "이미 좋아요를 누른 계정입니다.");
////        } else { // 해당 게시글에 좋아요를 누르지 않은 아이디이면 좋아요 처리
////
////            Like like = new Like(user, post);
////            likeRepository.save(like);
////        }
////
////        //게시글의 좋아요 개수 처리
////        post.setLikeCount((long) likeRepository.findByPostId(id).size());
////
////        return new ApiResponseDto(HttpStatus.OK.value(), "좋아요를 눌렀습니다.");
////    }
////
////    //게시글 좋아요 취소 API
////    @Transactional
////    public ApiResponseDto deleteLikePost(Long id, User user) {
////        //해당 게시글이 존재하는지 확인
////        Post post = postRepository.findById(id).orElseThrow(() -> new NullPointerException("Could Not found post"));
////
////        //해당 게시글에 좋아요를 누른 아이디인지 체크
////        Like checkLike = likeRepository.findByUserUserIdAndPostId(user.getUserId(), id);
////        if(checkLike != null) {
////            likeRepository.delete(checkLike);
////        } else { // 해당 게시글에 좋아요를 누르지 않은 아이디이면 좋아요 처리
////            return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "아직 좋아요를 누르지 않은 계정입니다.");
////        }
////
////        //게시글의 좋아요 개수 처리
////        post.setLikeCount((long) likeRepository.findByPostId(id).size());
////
////        return new ApiResponseDto(HttpStatus.OK.value(), "좋아요를 취소했습니다.");
////    }
//
//    @Service
//    @RequiredArgsConstructor
//    public class LikeService {
//
//        private final PostLikesRepository postLikesRepository;
//        private final PostRepository postRepository;
//
//        public ApiResponseDto likePost(Long postId, User user) {
//            try {
//                Post post = postRepository.findById(postId)
//                        .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));
//
//                // 사용자가 이미 게시물을 좋아요한 경우 확인
//                PostLikes existingPostLikes = postLikesRepository.findByUserAndPost(user, post);
//
//                if (existingPostLikes != null && existingPostLikes.getIsLiked()) {
//                    throw new IllegalArgumentException("이미 좋아요를 누른 게시물입니다.");
//                } else {
//                    // 사용자가 게시물을 좋아요하지 않은 경우, 좋아요 추가
//                    PostLikes postLikes = new PostLikes(user, post, true);
//                    postLikesRepository.save(postLikes);
//                    post.setLikeCount(post.getLikeCount() + 1);
//                }
//
//                return new ApiResponseDto(HttpStatus.CREATED.value(), "좋아요 클릭");
//            } catch (IllegalArgumentException e) {
//                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
//            }
//        }
//
//        public ApiResponseDto deleteLikePost(Long postId, User user) {
//            try {
//                Post post = postRepository.findById(postId)
//                        .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));
//
//                // 사용자가 게시물을 좋아요한 경우 확인
//                PostLikes existingPostLikes = postLikesRepository.findByUserAndPost(user, post);
//
//                if (existingPostLikes != null && existingPostLikes.getIsLiked()) {
//                    // 사용자가 게시물을 좋아요한 경우, 좋아요 취소
//                    existingPostLikes.setIsLiked(false);
//                    postLikesRepository.save(existingPostLikes);
//                    post.setLikeCount(post.getLikeCount() - 1);
//                    return new ApiResponseDto(HttpStatus.OK.value(), "좋아요 취소");
//                } else {
//                    throw new IllegalArgumentException("아직 좋아요를 누르지 않은 게시물입니다.");
//                }
//            } catch (IllegalArgumentException e) {
//                return new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
//            }
//        }
//    }
//}
//
//
//
