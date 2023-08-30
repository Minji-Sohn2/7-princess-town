package com.example.princesstown.controller.boardController;

import com.example.princesstown.dto.request.BoardRequestDto;
import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.dto.response.BoardResponseDto;
import com.example.princesstown.security.user.UserDetailsImpl;
import com.example.princesstown.service.board.BoardService;
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

    // 게시판 생성 API
    @PostMapping("/boards")
    @ResponseBody
    public ResponseEntity<ApiResponseDto> createBoard(@RequestBody BoardRequestDto boardRequestDto,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("title : " + boardRequestDto.getTitle());

        boardService.createBoard(boardRequestDto, userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.CREATED.value(), "글 작성에 성공했습니다."));
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
