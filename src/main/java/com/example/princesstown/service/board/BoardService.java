package com.example.princesstown.service.board;

import com.example.princesstown.dto.request.BoardRequestDto;
import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.dto.response.BoardResponseDto;
import com.example.princesstown.entity.Board;
import com.example.princesstown.entity.User;
import com.example.princesstown.repository.board.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    // private final JwtUtil jwtUtil;

    // 게시글 전체 조회 API
    @Transactional(readOnly = true)
    public List<BoardResponseDto> getBoard() {
        List<Board> boards = boardRepository.findAllByOrderByCreatedAtDesc();
        List<BoardResponseDto> boardResponseDto = new ArrayList<>();

        for(Board board : boards){
            boardResponseDto.add(new BoardResponseDto(board));
        }

        return boardResponseDto;
    }

    // 게시글 선택 조회 API
    @Transactional(readOnly = true)
    public BoardResponseDto getBoard(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("선택하신 게시판은 존재하지 않습니다.")
        );

        return new BoardResponseDto(board);

    }

    // 게시글 등록 API
    public BoardResponseDto createBoard(BoardRequestDto boardRequestDto, User user) {
        if(user == null){
            throw new IllegalArgumentException("허가되지 않은 사용자입니다.");
        }

        Board board = new Board(boardRequestDto, user);
        boardRepository.save(board);

        return new BoardResponseDto(board);
    }

    // 게시글 수정 API
    @Transactional
    public BoardResponseDto updateBoard(Long id, BoardRequestDto boardRequestDto, User user) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new NullPointerException("선택하신 게시판은 존재하지 않습니다.")
        );

        if(board.getUser().getUserId().equals(user.getUserId())){ // || user.getRole().equals(UserRoleEnum.ADMIN)
            board.update(boardRequestDto);
        } else {
            throw new IllegalArgumentException("작성자만 수정이 가능합니다.");
        }

        return new BoardResponseDto(board);
    }


    // 게시글 삭제 API
    @Transactional
    public ApiResponseDto deleteBoard(Long id, User user) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new NullPointerException("선택하신 게시물은 존재하지 않습니다.")
        );
        if(board.getUser().getUserId().equals(user.getUserId())){ //  || user.getRole().equals(UserRoleEnum.ADMIN)
            boardRepository.delete(board);
        } else {
            return new ApiResponseDto(400, "작성자만 삭제가 가능합니다.");
        }
        return new ApiResponseDto(200, "삭제가 완료되었습니다.");
    }
}
