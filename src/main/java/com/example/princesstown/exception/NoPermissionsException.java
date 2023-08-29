package com.example.princesstown.exception;

// 수정, 삭제 권한이 없는 사용자일 경우
public class NoPermissionsException extends RuntimeException {
    public NoPermissionsException(String message) {
        super(message);
    }
}
