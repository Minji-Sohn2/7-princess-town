package com.example.princesstown.dto.comment;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDto {
    @Size(min = 3, max = 1000,
            message = "최소 3자이상 최대 1000자 이하로 작성해주세요.")
    private String content;

    private String emoji;
}
