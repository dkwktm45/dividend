package com.example.dividend.exception.impl;

import com.example.dividend.exception.Abstractexception;
import org.springframework.http.HttpStatus;

public class YahooScraperException extends Abstractexception {
  @Override
  public int getStatusCode() {
    return HttpStatus.BAD_REQUEST.value();
  }

  @Override
  public String getMessage() {
    return "야후 정보를 가져오는 동안 발생한 에러입니다.";
  }
}
