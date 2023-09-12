package com.example.princesstown.service.board;

import com.example.princesstown.dto.request.BoardRequestDto;
import com.example.princesstown.dto.response.ApiResponseDto;
import com.example.princesstown.dto.response.BoardResponseDto;
import com.example.princesstown.dto.response.PostResponseDto;
import com.example.princesstown.entity.Board;
import com.example.princesstown.entity.User;
import com.example.princesstown.repository.board.BoardRepository;
import com.example.princesstown.repository.post.PostRepository;
import com.example.princesstown.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final PostService postService;
    private final PostRepository postRepository;

    // private final JwtUtil jwtUtil;

    // 게시판 전체 조회 API
    @Transactional(readOnly = true)
    public List<BoardResponseDto> getBoard() {
        List<Board> boards = boardRepository.findAllByOrderByCreatedAtDesc();
        List<BoardResponseDto> boardResponseDto = new ArrayList<>();

        for(Board board : boards){
            boardResponseDto.add(new BoardResponseDto(board));
        }

        return boardResponseDto;
    }

    //게시판 내 위치반경 게시글 조회 API
    @Transactional(readOnly = true)
    public BoardResponseDto getBoardWithNearbyPosts(Long id, Long userId) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("선택하신 게시판은 존재하지 않습니다.")
        );

        // 게시판 내 위치반경 내 게시글 조회
        List<PostResponseDto> nearbyPostsInBoard = postService.getPostsAroundUserByBoardId(id, userId);

        // BoardResponseDto에 위치 반경 내 게시글 목록 추가
        BoardResponseDto responseDto = new BoardResponseDto(board);
        responseDto.setNearbyPosts(nearbyPostsInBoard);

        // "postList" 필드를 null로 설정하여 반환하지 않습니다.
        responseDto.setPostList(null);

        return responseDto;
    }

    // 게시판 선택 조회 API
    @Transactional(readOnly = true)
    public BoardResponseDto getBoard(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("선택하신 게시판은 존재하지 않습니다.")
        );

        return new BoardResponseDto(board);

    }

    // 위치반경 내 게시판 선택 조회 API

    // 게시판 등록 API
    public BoardResponseDto createBoard(BoardRequestDto boardRequestDto, User user) {
        if(user == null){
            throw new IllegalArgumentException("허가되지 않은 사용자입니다.");
        }

        Board board = new Board(boardRequestDto, user);
        boardRepository.save(board);

        return new BoardResponseDto(board);
    }

    // 게시판 수정 API
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


    // 게시판 삭제 API
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
