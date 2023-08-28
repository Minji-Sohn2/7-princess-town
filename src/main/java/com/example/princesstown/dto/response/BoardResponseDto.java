package com.example.princesstown.dto.response;

import com.example.princesstown.entity.Board;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class BoardResponseDto {
    private Long id;
    private String title;
    private  List<PostResponseDto> postList;

    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.postList = board.getPostList().stream().map(PostResponseDto::new).collect(Collectors.toList());
    }
}
