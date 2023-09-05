package com.example.princesstown.dto.comment;

import com.example.princesstown.service.comment.PaginationInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PagedReplyResponseDto {

    private List<ReplyResponseDto> replys;
    private PaginationInfo paginationInfo;

    public PagedReplyResponseDto(List<ReplyResponseDto> replys) {
        this.replys = replys;
    }
}
