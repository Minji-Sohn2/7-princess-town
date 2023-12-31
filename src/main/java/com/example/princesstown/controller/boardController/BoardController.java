package com.example.princesstown.controller.boardController;

import com.example.princesstown.dto.request.BoardRequestDto;
import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.dto.response.BoardResponseDto;
import com.example.princesstown.security.user.UserDetailsImpl;
import com.example.princesstown.service.board.BoardService;
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
public class BoardController {
    private final BoardService boardService;
    private final PostService postService;


    // 게시판 전체 조회 API
    @GetMapping("/boards")
    public List<BoardResponseDto> getBoard() {
        return boardService.getBoard();
    }


    // 게시판 선택 조회 API
    @GetMapping("/boards/{boardId}")
    public BoardResponseDto getBoard(@PathVariable Long boardId) {
        return boardService.getBoard(boardId);
    }

    // 위치반경 내 게시판 선택 조회 API
    @GetMapping("/boards/{boardId}/radiusposts")
    public ResponseEntity<BoardResponseDto> getBoardWithNearbyPosts(
            @PathVariable Long boardId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        if (userDetails != null) {
            // 현재 인증된 사용자의 정보를 가져오기
            Long userId = userDetails.getUser().getUserId();

            // 게시판 내 위치 반경 내 게시글 조회
            BoardResponseDto boardResponseDto = boardService.getBoardWithNearbyPosts(boardId, userId);

            return ResponseEntity.ok(boardResponseDto);
        } else {
            // 사용자가 인증되지 않은 경우 401 Unauthorized 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    // 게시판 생성 API
    @PostMapping("/boards")
    @ResponseBody
    public ResponseEntity<ApiResponseDto> createBoard(@RequestBody BoardRequestDto boardRequestDto,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("title : " + boardRequestDto.getTitle());

        boardService.createBoard(boardRequestDto, userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.CREATED.value(), "게시판 작성에 성공했습니다."));
    }

    // 게시판 수정 API
    @PutMapping("/boards/{boardId}")
    public ResponseEntity<ApiResponseDto> updateBoard(@PathVariable Long boardId,
                                                      @RequestBody BoardRequestDto boardRequestDto,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            boardService.updateBoard(boardId, boardRequestDto, userDetails.getUser());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "게시판 수정에 실패했습니다."));
        }
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "게시판 수정에 성공했습니다."));
    }


    // 게시판 삭제 API
    @DeleteMapping("/boards/{boardId}")
    public ApiResponseDto deletePost(@PathVariable Long boardId,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return boardService.deleteBoard(boardId, userDetails.getUser());
    }
}
