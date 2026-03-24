package com.pawpawlog.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

  private final boolean success;
  private final String message;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final T data;

  public static ApiResponse<Void> ok() {
    return new ApiResponse<>(true, "요청이 성공적으로 처리되었습니다.", null);
  }

  public static <T> ApiResponse<T> ok(T data) {
    return new ApiResponse<>(true, "요청이 성공적으로 처리되었습니다.", data);
  }

  public static ApiResponse<Void> ok(String message) {
    return new ApiResponse<>(true, message, null);
  }

  public static <T> ApiResponse<T> ok(String message, T data) {
    return new ApiResponse<>(true, message, data);
  }

  public static ApiResponse<Void> error(String message) {
    return new ApiResponse<>(false, message, null);
  }

  public static <T> ApiResponse<T> error(String message, T data) {
    return new ApiResponse<>(false, message, data);
  }
}