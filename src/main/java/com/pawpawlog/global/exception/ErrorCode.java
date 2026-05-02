package com.pawpawlog.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  // User
  DUPLICATE_USERNAME(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다."),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),
  INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
  TAG_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "태그 생성에 실패했습니다."),

  // Auth
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
  FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

  // Token
  MISSING_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 존재하지 않습니다."),
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
  EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),

  // Request
  INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
  NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),

  // Server
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

  private final HttpStatus status;
  private final String message;
}
