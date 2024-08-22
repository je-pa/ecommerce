package com.ecommerce.global.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {

  // BAD_REQUEST:400:잘못된요청

  // Unauthorized:401:인증이슈
  INVALID_JWT_SIGNATURE(UNAUTHORIZED, "유효하지 않는 JWT 서명입니다."),
  EXPIRED_JWT_TOKEN(UNAUTHORIZED, "만료된 JWT token 입니다."),
  UNSUPPORTED_JWT_TOKEN(UNAUTHORIZED, "지원되지 않는 JWT 토큰입니다."),
  JWT_CLAIMS_EMPTY(UNAUTHORIZED, "잘못된 JWT 토큰입니다."),
  AUTHENTICATION_METHOD_NOT_SUPPORTED(UNAUTHORIZED, "지원하지 않는 인증 메서드입니다."),
  CONTENT_TYPE_NOT_SUPPORTED(UNAUTHORIZED, "지원하지 않는 인증 컨텐트 타입입니다."),

  // FORBIDDEN:403:권한이슈

  // NOT_FOUND:404:자원없음
  USER_NOT_FOUND(NOT_FOUND, "유저 개체를 찾지 못했습니다."),

  // CONFLICT:409:충돌

  // PAYLOAD_TOO_LARGE:413:파일 크기 초과
  // UNPROCESSABLE_ENTITY:422:의미론적 오류
  // INTERNAL_SERVER_ERROR:500:서버 문제 발생
  ;
  private final HttpStatus status;
  private final String message;

}