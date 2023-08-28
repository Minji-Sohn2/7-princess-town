package com.example.princesstown.dto.comment;

import com.example.princesstown.service.comment.PaginationInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PagedCommentResponseDto {

    private List<CommentResponseDto> comments;
    private PaginationInfo paginationInfo;

    public PagedCommentResponseDto(List<CommentResponseDto> comments) {
        this.comments = comments;
    }

    public PagedCommentResponseDto(List<CommentResponseDto> comments, PaginationInfo paginationInfo) {
        this.comments = comments;
        this.paginationInfo = paginationInfo;
    }
}
