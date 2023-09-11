package com.example.dividend.exception;

public abstract class Abstractexception extends RuntimeException{
  abstract public int getStatusCode();

  abstract public String getMessage();
}
