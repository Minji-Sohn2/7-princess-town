package com.example.princesstown.dto.response;

import com.example.princesstown.entity.Board;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
public class BoardResponseDto {
    private Long id;
    private String title;
    private  List<PostResponseDto> postList;
    private List<PostResponseDto> nearbyPosts;

    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.postList = board.getPostList().
                stream().
                sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt())). // 최신순 정렬
                map(PostResponseDto::new).
                collect(Collectors.toList());
    }

    public void setNearbyPosts(List<PostResponseDto> nearbyPosts) {
        this.nearbyPosts = nearbyPosts;
    }
}
