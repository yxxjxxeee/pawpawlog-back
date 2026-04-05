package com.pawpawlog.global.util;

import com.pawpawlog.global.exception.ErrorCode;
import com.pawpawlog.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpResponseUtil {

  public static <T> void writeSuccessResponse(HttpServletResponse response, HttpStatus status,
      T data, ObjectMapper objectMapper) throws IOException {
    response.setStatus(status.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.success(data)));
  }

  public static void writeErrorResponse(HttpServletResponse response, ErrorCode errorCode,
      ObjectMapper objectMapper) throws IOException {
    response.setStatus(errorCode.getStatus().value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    response.getWriter()
        .write(objectMapper.writeValueAsString(
            ApiResponse.error(errorCode.name(), errorCode.getMessage())));
  }
}