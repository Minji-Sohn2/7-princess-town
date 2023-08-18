package com.example.princesstown.exception;

import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;

public class TokenNotValidateException extends JwtException {
    public TokenNotValidateException(String message) {
        super(message);
    }
}
