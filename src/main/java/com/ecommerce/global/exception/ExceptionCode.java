package com.ecommerce.global.exception;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
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

  // FORBIDDEN:403:권한이슈

  // NOT_FOUND:404:자원없음
  USER_NOT_FOUND(NOT_FOUND, "유저 개체를 찾지 못했습니다."),

  // CONFLICT:409:충돌
  EMAIL_ALREADY_EXISTS(CONFLICT, "중복된 이메일 입니다."),

  // PAYLOAD_TOO_LARGE:413:파일 크기 초과
  // UNPROCESSABLE_ENTITY:422:의미론적 오류
  // INTERNAL_SERVER_ERROR:500:서버 문제 발생
  EMAIL_SENDING_FAILED(INTERNAL_SERVER_ERROR, "이메일 전송에 실패했습니다."),
  EMAIL_AUTH_CODE_SAVE_FAILED(INTERNAL_SERVER_ERROR, "이메일 인증 코드 저장에 실패했습니다."),
  NO_SUCH_ALGORITHM(INTERNAL_SERVER_ERROR, "지정된 알고리즘을 찾을 수 없습니다."),
  NO_SUCH_PADDING(INTERNAL_SERVER_ERROR, "지정된 패딩 방식을 찾을 수 없습니다."),
  INVALID_KEY(INTERNAL_SERVER_ERROR, "잘못된 키입니다."),
  ILLEGAL_BLOCK_SIZE(INTERNAL_SERVER_ERROR, "잘못된 블록 크기입니다."),
  BAD_PADDING(INTERNAL_SERVER_ERROR, "잘못된 패딩입니다."),
  ;
  private final HttpStatus status;
  private final String message;

}