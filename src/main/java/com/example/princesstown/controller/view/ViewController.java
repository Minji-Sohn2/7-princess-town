package com.example.princesstown.controller.view;

import com.example.princesstown.dto.response.BoardResponseDto;
import com.example.princesstown.dto.response.PostResponseDto;
import com.example.princesstown.repository.board.BoardRepository;
import com.example.princesstown.security.user.UserDetailsImpl;
import com.example.princesstown.service.board.BoardService;
import com.example.princesstown.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/view")
@RequiredArgsConstructor
public class ViewController {

    private final BoardService boardService;
    private final PostService postService;
    private final BoardRepository boardRepository;

    //메인페이지
    @GetMapping("/mainpage")
    public String viewMainPage(Model model) {
        //게시판 목록
        List<BoardResponseDto> boardList = boardService.getBoard();
        model.addAttribute("boardList", boardList);

        //인기 검색어
        List<PostResponseDto> topPosts = postService.getTop10LikedPostsWithDuplicates();
        model.addAttribute("topPosts", topPosts);

        //전체 게시글
        List<PostResponseDto> posts = postService.getPostsPage(0);
        model.addAttribute("posts", posts);

        return "mainpage";
    }

    // 게시판 내 위치반경 내 게시글 조회 뷰
    @GetMapping("/boards/{boardId}/radiusposts")
    public String viewRadiusPostsInBoard(
            @PathVariable Long boardId,
            Model model,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        // 게시판 목록
        List<BoardResponseDto> boardList = boardService.getBoard();
        model.addAttribute("boardList", boardList);

        // 인기 검색어
        List<PostResponseDto> topPosts = postService.getTop10LikedPostsWithDuplicates();
        model.addAttribute("topPosts", topPosts);

        BoardResponseDto boardTitle = boardService.getBoardTitle(boardId);
        String getTitle = boardTitle.getTitle();
        model.addAttribute("title", getTitle);

        if (userDetails != null) {
            // 위치 반경 내 게시글 조회
            Long userId = userDetails.getUser().getUserId();

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            model.addAttribute("authentication", authentication);

            // 게시판 내 위치반경 내 게시글 조회
            BoardResponseDto nearbyPostsBoard = boardService.getBoardWithNearbyPosts(boardId, userId);
            model.addAttribute("selectedBoard", nearbyPostsBoard);
        }

        return "nearbyPostsInBoard"; // 프론트엔드에서 사용할 뷰 이름
    }

    // 위치 반경 내 게시글 조회
    @GetMapping("/radiusposts")
    public String viewRadiusPosts(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        //게시판 목록
        List<BoardResponseDto> boardList = boardService.getBoard();
        model.addAttribute("boardList", boardList);

        //인기 검색어
        List<PostResponseDto> topPosts = postService.getTop10LikedPostsWithDuplicates();
        model.addAttribute("topPosts", topPosts);

        if (userDetails != null) {
            // 위치 반경 내 게시글 조회
            Long userId = userDetails.getUser().getUserId();

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            model.addAttribute("authentication", authentication);

            List<PostResponseDto> nearbyPosts = postService.getPostsAroundUser(userId);
            model.addAttribute("radiusPosts", nearbyPosts);
        }
        return "radiusposts";
    }

    //제목으로 검색
    @GetMapping("/searchTitle")
    public String searchPostsByTitle(@RequestParam String title, Model model) {
        //게시판 목록
        List<BoardResponseDto> boardList = boardService.getBoard();
        model.addAttribute("boardList", boardList);

        //제목으로 검색한 게시글
        List<PostResponseDto> searchTitleResults = postService.searchPostsByTitle(title);
        model.addAttribute("searchTitleResults", searchTitleResults);

        return "searchTitle";
    }

    //내용으로 검색
    @GetMapping("/searchContent")
    public String searchPostsByContents(@RequestParam String contents, Model model) {
        //게시판 목록
        List<BoardResponseDto> boardList = boardService.getBoard();
        model.addAttribute("boardList", boardList);

        //내용으로 검색한 게시글
        List<PostResponseDto> searchContentsResults = postService.searchPostsByContents(contents);
        model.addAttribute("searchContentsResults", searchContentsResults);

        return "searchContent";
    }

    //제목 + 내용으로 검색
    @GetMapping("/searchKeyword")
    public String searchPostsByTitleAndContents(@RequestParam String keyword, Model model) {
        //게시판 목록
        List<BoardResponseDto> boardList = boardService.getBoard();
        model.addAttribute("boardList", boardList);

        //제목+내용으로 검색한 게시글
        List<PostResponseDto> searchKeywordResults = postService.searchPostsByTitleOrContents(keyword);
        model.addAttribute("searchKeywordResults", searchKeywordResults);

        return "searchKeyword";
    }

    //상세 게시글 조회
    @GetMapping("/posts/{postId}")
    public String viewPost(@PathVariable Long postId, Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        //게시판 목록
        List<BoardResponseDto> boardList = boardService.getBoard();
        model.addAttribute("boardList", boardList);
        //상세 게시글
        PostResponseDto post = postService.getPost(postId);
        postService.incrementViewCount(postId); // 이 부분 추가
        model.addAttribute("post", post);

        return "postDetails"; // 이 부분은 HTML 템플릿의 이름과 일치해야 합니다.
    }

    //지역게시판 상세 게시글 조회
    @GetMapping("/radiusposts/{postId}")
    public String viewradiusPost(@PathVariable Long postId, Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        //게시판 목록
        List<BoardResponseDto> boardList = boardService.getBoard();
        model.addAttribute("boardList", boardList);
        //상세 게시글
        PostResponseDto post = postService.getPost(postId);
        postService.incrementViewCount(postId); // 이 부분 추가
        model.addAttribute("post", post);

        return "radiuspostDetails"; // 이 부분은 HTML 템플릿의 이름과 일치해야 합니다.
    }

    // 게시판 선택 조회 API
    @GetMapping("/boards/{boardId}")
    public String viewBoard(@PathVariable Long boardId, Model model) {
        //게시판 목록
        List<BoardResponseDto> boardList = boardService.getBoard();
        model.addAttribute("boardList", boardList);

        BoardResponseDto selectedBoard = boardService.getBoard(boardId);
        model.addAttribute("selectedBoard", selectedBoard);

        BoardResponseDto boardTitle = boardService.getBoardTitle(boardId);
        String getTitle = boardTitle.getTitle();
        model.addAttribute("title", getTitle);

        return "detailboard"; // 프론트엔드에서 사용할 뷰 이름
    }

    //게시글 작성
    @GetMapping("/createpost")
    public String createPostView(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        List<BoardResponseDto> boardList = boardService.getBoard();
        model.addAttribute("boardList", boardList);

        return "writePost"; // HTML 템플릿 파일의 이름과 일치해야 합니다.
    }

    @GetMapping("/editpost/{podstId}")
    public String editPostView(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        List<BoardResponseDto> boardList = boardService.getBoard();
        model.addAttribute("boardList", boardList);

        return "editpost"; // HTML 템플릿 파일의 이름과 일치해야 합니다.
    }

    // 채팅 리스트 화면
    @GetMapping("/chatRooms")
    public String rooms() {
        return "chat/myChatRooms";
    }

    // 채팅방 입장 화면
    @GetMapping("/chatRooms/{roomId}")
    public String roomDetail(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId);
        return "chat/chatRoomDetail";
    }
}
