package com.pawpawlog.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

  @Schema(description = "에러 코드")
  private final String code;

  @Schema(description = "에러 메시지")
  private final String message;

  @Schema(description = "추가 데이터 (유효성 오류 시 필드별 메시지 포함)")
  private final Object data;

  private ErrorResponse(String code, String message, Object data) {
    this.code = code;
    this.message = message;
    this.data = data;
  }

  public static ErrorResponse error(String code, String message) {
    return new ErrorResponse(code, message, null);
  }

  public static ErrorResponse error(String code, String message, Object data) {
    return new ErrorResponse(code, message, data);
  }
}

