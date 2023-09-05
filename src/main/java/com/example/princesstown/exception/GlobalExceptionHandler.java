package com.example.princesstown.exception;

import com.example.princesstown.dto.response.ApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ApiResponseDto> illegalArgumentExceptionHandler(IllegalArgumentException ex) {
        ApiResponseDto apiResponseDto = new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.badRequest().body(apiResponseDto);
    }

    @ExceptionHandler({NullPointerException.class})
    public ResponseEntity<ApiResponseDto> nullPointerExceptionHandler(NullPointerException ex) {
        ApiResponseDto apiResponseDto = new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.badRequest().body(apiResponseDto);
    }

    @ExceptionHandler({NoPermissionsException.class})
    public ResponseEntity<ApiResponseDto> noPermissionsExceptionHandler(NoPermissionsException ex) {
        ApiResponseDto apiResponseDto = new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.badRequest().body(apiResponseDto);
    }
}
