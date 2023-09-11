package com.example.dividend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {
  @ExceptionHandler(Abstractexception.class)
  protected ResponseEntity<?> handlerCustomException(Abstractexception e) {
    ErrorResponse errorResponse = ErrorResponse.builder()
        .code(e.getStatusCode())
        .message(e.getMessage()).build();
    return ResponseEntity.badRequest().body(errorResponse);
  }
}
