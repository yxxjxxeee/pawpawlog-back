package com.pawpawlog.global.exception;

import com.pawpawlog.global.response.ApiResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomException.class)
  protected ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {

    ErrorCode errorCode = e.getErrorCode();
    log.warn("CustomException: {}", errorCode.name());

    return ResponseEntity
        .status(errorCode.getStatus())
        .body(ApiResponse.error(
            errorCode.name(),
            errorCode.getMessage()
        ));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(
      MethodArgumentNotValidException e) {

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
        .body(ApiResponse.error(
            errorCode.name(),
            errorCode.getMessage(),
            errors
        ));
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<ApiResponse<Void>> handleException(Exception e) {

    ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

    log.error("Unhandled exception", e);

    return ResponseEntity
        .status(errorCode.getStatus())
        .body(ApiResponse.error(
            errorCode.name(),
            errorCode.getMessage()
        ));
  }
}