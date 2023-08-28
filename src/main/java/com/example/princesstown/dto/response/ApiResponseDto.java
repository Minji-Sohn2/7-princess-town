package com.example.princesstown.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDto {

    private int status;
    private String message;
    private Object data;

    public ApiResponseDto(int status, String message) {
        this.status = status;
        this.message = message;
        this.data = null;
    }

    public ApiResponseDto(int status,String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}

