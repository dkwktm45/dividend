package com.example.dividend.exception.impl;

import com.example.dividend.exception.Abstractexception;
import org.springframework.http.HttpStatus;

public class GetTickerFailException extends Abstractexception {
  @Override
  public int getStatusCode() {
    return HttpStatus.BAD_REQUEST.value();
  }

  @Override
  public String getMessage() {
    return "ticker를 가져오는데 실패했습니다.";
  }
}
