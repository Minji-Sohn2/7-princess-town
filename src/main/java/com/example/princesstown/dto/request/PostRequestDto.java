package com.example.princesstown.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PostRequestDto {

    @NotBlank(message = "필수 입력 값입니다.")
    private String title; // 게시글 제목
    @NotBlank(message = "필수 입력 값입니다.")
    private String contents; // 게시글 내용

}