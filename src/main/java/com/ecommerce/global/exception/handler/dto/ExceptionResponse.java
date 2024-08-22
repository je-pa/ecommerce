package com.ecommerce.global.exception.handler.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ExceptionResponse {

  private final int code;
  private final HttpStatus status;
  private final String message;

  private ExceptionResponse(HttpStatus status, Exception ex) {
    this.code = status.value();
    this.status = status;
    this.message = ex.getMessage();
  }

  private ExceptionResponse(HttpStatus status, String message) {
    this.code = status.value();
    this.status = status;
    this.message = message;
  }

  public static ExceptionResponse of(HttpStatus httpStatus, Exception ex) {
    return new ExceptionResponse(httpStatus, ex);
  }

  public static ExceptionResponse of(HttpStatus httpStatus, String message) {
    return new ExceptionResponse(httpStatus, message);
  }
}
