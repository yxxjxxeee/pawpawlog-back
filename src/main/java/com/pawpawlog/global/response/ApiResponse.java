package com.pawpawlog.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

  private final boolean success;
  private final int statusCode;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final String message;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final T data;

  public static ApiResponse<Void> ok() {
    return new ApiResponse<>(true, 200, null, null);
  }

  public static <T> ApiResponse<T> ok(T data) {
    return new ApiResponse<>(true, 200, null, data);
  }

  public static ApiResponse<Void> ok(String message) {
    return new ApiResponse<>(true, 200, message, null);
  }

  public static <T> ApiResponse<T> ok(String message, T data) {
    return new ApiResponse<>(true, 200, message, data);
  }

  public static ApiResponse<Void> error(int statusCode, String message) {
    return new ApiResponse<>(false, statusCode, message, null);
  }

  public static <T> ApiResponse<T> error(int statusCode, String message, T data) {
    return new ApiResponse<>(false, statusCode, message, data);
  }
}