package com.pawpawlog.global.exception;

import com.pawpawlog.global.response.ErrorResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomException.class)
  protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
    ErrorCode errorCode = e.getErrorCode();
    log.warn("커스텀 예외 발생: {}", errorCode.name());
    return ResponseEntity
        .status(errorCode.getStatus())
        .body(ErrorResponse.error(errorCode.name(), errorCode.getMessage()));
  }

  @ExceptionHandler(MissingRequestHeaderException.class)
  protected ResponseEntity<ErrorResponse> handleMissingHeader(MissingRequestHeaderException e) {
    ErrorCode errorCode = ErrorCode.INVALID_INPUT;
    log.warn("필수 헤더 누락: {}", e.getHeaderName());
    return ResponseEntity
        .status(errorCode.getStatus())
        .body(ErrorResponse.error(errorCode.name(), e.getMessage()));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  protected ResponseEntity<ErrorResponse> handleTypeMismatch(
      MethodArgumentTypeMismatchException e) {
    ErrorCode errorCode = ErrorCode.INVALID_INPUT;
    log.warn("파라미터 타입 불일치: {} = {}", e.getName(), e.getValue());
    return ResponseEntity
        .status(errorCode.getStatus())
        .body(ErrorResponse.error(errorCode.name(), errorCode.getMessage()));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  protected ResponseEntity<ErrorResponse> handleMessageNotReadable(
      HttpMessageNotReadableException e) {
    ErrorCode errorCode = ErrorCode.INVALID_INPUT;
    log.warn("요청 본문 파싱 실패: {}", e.getMessage());
    return ResponseEntity
        .status(errorCode.getStatus())
        .body(ErrorResponse.error(errorCode.name(), errorCode.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
    ErrorCode errorCode = ErrorCode.INVALID_INPUT;

    Map<String, String> errors = e.getBindingResult()
        .getFieldErrors()
        .stream()
        .collect(Collectors.toMap(
            FieldError::getField,
            FieldError::getDefaultMessage,
            (a, b) -> a + ", " + b,
            LinkedHashMap::new
        ));

    e.getBindingResult()
        .getFieldErrors()
        .forEach(f -> log.warn("{}: {}", f.getField(), f.getDefaultMessage()));

    return ResponseEntity
        .status(errorCode.getStatus())
        .body(ErrorResponse.error(errorCode.name(), errorCode.getMessage(), errors));
  }

  @ExceptionHandler(NoResourceFoundException.class)
  protected ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException e) {
    ErrorCode errorCode = ErrorCode.NOT_FOUND;
    log.warn("존재하지 않는 리소스 요청: {}", e.getMessage());
    return ResponseEntity
        .status(errorCode.getStatus())
        .body(ErrorResponse.error(errorCode.name(), errorCode.getMessage()));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  protected ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
      DataIntegrityViolationException e) {
    ErrorCode errorCode = ErrorCode.DATA_CONFLICT;
    log.warn("데이터 무결성 제약 위반: {}", e.getMessage());
    return ResponseEntity
        .status(errorCode.getStatus())
        .body(ErrorResponse.error(errorCode.name(), errorCode.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<ErrorResponse> handleException(Exception e) {
    ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
    log.error("처리되지 않은 예외 발생", e);
    return ResponseEntity
        .status(errorCode.getStatus())
        .body(ErrorResponse.error(errorCode.name(), errorCode.getMessage()));
  }
}
