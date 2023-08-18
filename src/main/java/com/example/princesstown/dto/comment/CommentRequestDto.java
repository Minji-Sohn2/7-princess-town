package com.example.princesstown.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDto {
    @NotBlank(message = "내용이 입력되지 않었습니다.")
    private String content;

    private String emoji;
}
