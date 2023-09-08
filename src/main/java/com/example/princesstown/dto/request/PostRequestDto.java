package com.example.princesstown.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostRequestDto {

    @NotBlank(message = "필수 입력 값입니다.")
    private String title; // 게시글 제목
    @NotBlank(message = "필수 입력 값입니다.")
    private String contents; // 게시글 내용

    private MultipartFile postImage; // 업로드된 이미지 파일

    private MultipartFile newPostImage;

    private String postImageUrl; // 이미지 URL
    private Double latitude;
    private Double longitude;

}