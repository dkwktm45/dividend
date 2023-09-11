package com.example.dividend.exception.impl;

import com.example.dividend.exception.Abstractexception;
import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends Abstractexception {
  @Override
  public int getStatusCode() {
    return HttpStatus.BAD_REQUEST.value();
  }

  @Override
  public String getMessage() {
    return "비밀번호가 일치하지 않습니다.";
  }
}
