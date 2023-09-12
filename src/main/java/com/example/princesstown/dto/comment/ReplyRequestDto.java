package com.example.princesstown.dto.comment;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReplyRequestDto {

    @Size(min = 3, max = 250)
    private String content;

    private String emoji;
}
