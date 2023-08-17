package com.example.princesstown.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RestApiResponseDto {

    private int status;
    private String success;

    private Object result;

    public RestApiResponseDto(int status, String success) {
        this.status = status;
        this.success = success;
    }
}
