package com.pawpawlog.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"success", "code", "message", "data"})
public class ApiResponse<T> {

  private final boolean success;
  private final String code;
  private final String message;
  private final T data;

  private ApiResponse(boolean success, String code, String message, T data) {
    this.success = success;
    this.code = code;
    this.message = message;
    this.data = data;
  }

  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(true, null, null, data);
  }

  public static ApiResponse<Void> success() {
    return new ApiResponse<>(true, null, null, null);
  }

  public static ApiResponse<Void> error(String code, String message) {
    return new ApiResponse<>(false, code, message, null);
  }

  public static <T> ApiResponse<T> error(String code, String message, T data) {
    return new ApiResponse<>(false, code, message, data);
  }
}