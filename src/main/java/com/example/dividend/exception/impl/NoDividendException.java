package com.example.dividend.exception.impl;

import com.example.dividend.exception.Abstractexception;
import org.springframework.http.HttpStatus;

public class NoDividendException extends Abstractexception {
  @Override
  public int getStatusCode() {
    return HttpStatus.BAD_REQUEST.value();
  }

  @Override
  public String getMessage() {
    return "배당금 정보가 존재하지 않습니다.";
  }
}
