package com.pawpawlog.global.security;

import com.pawpawlog.global.exception.CustomException;
import com.pawpawlog.global.exception.ErrorCode;
import com.pawpawlog.global.util.HttpResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException {

    Exception exception = (Exception) request.getAttribute("exception");

    ErrorCode errorCode;
    if (exception instanceof CustomException e) {
      errorCode = e.getErrorCode();
    } else {
      log.warn("인증 실패: {}", authException.getMessage());
      errorCode = ErrorCode.UNAUTHORIZED;
    }

    HttpResponseUtil.writeErrorResponse(response, errorCode, objectMapper);
  }
}