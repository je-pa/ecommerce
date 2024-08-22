package com.ecommerce.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException{
  @Getter
  private final HttpStatus statusCode;

  public static CustomException from(ExceptionCode exceptionCode) {
    return new CustomException(exceptionCode.getStatus(), exceptionCode.getMessage());
  }

  private CustomException(HttpStatus statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

}